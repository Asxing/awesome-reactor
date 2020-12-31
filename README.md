# 反应式编程介绍

## 反应式编程介绍

- 在传统的编程范式中，我们一般通过迭代器（Iterator）模式来遍历一个序列。这种遍历方式是由调用者来控制节奏的，采用的是拉的方式。每次由调用者通过 next()方法来获取序列中的下一个值。
- 使用反应式流时采用的则是推的方式，即常见的发布者-订阅者模式。当发布者有新的数据产生时，这些数据会被推送到订阅者来进行处理。
- 在反应式流上可以添加各种不同的操作来对数据进行处理，形成数据处理链。这个以声明式的方式添加的处理链只在订阅者进行订阅操作时才会真正执行。

## Reactor 简介

- RxJava 2 在设计和实现时考虑到了与规范的整合，不过为了保持与 RxJava 的兼容性，很多地方在使用时也并不直观。
- Reactor 则是完全基于反应式流规范设计和实现的库，没有 RxJava 那样的历史包袱，在使用上更加的直观易懂。
- Reactor 也是 Spring 5 中反应式编程的基础。学习和掌握 Reactor 可以更好地理解 Spring 5 中的相关概念。

## Flux 和 Mono

### 创建 Flux

- Flux 类的静态方法

    - just()：可以指定序列中包含的全部元素。创建出来的 Flux 序列在发布这些元素之后会自动结束。

    - fromArray()，fromIterable()和 fromStream()：可以从一个数组、Iterable 对象或 Stream 对象中创建 Flux 对象。

    - empty()：创建一个不包含任何元素，只发布结束消息的序列。

    - error(Throwable error)：创建一个只包含错误消息的序列。

    - never()：创建一个不包含任何消息通知的序列。

    - range(int start, int count)：创建包含从 start 起始的 count 个数量的 Integer 对象的序列。

    - interval(Duration period)和 interval(Duration delay, Duration period)：创建一个包含了从 0 开始递增的 Long 对象的序列。其中包含的元素按照指定的间隔来发布。除了间隔时间之外，还可以指定起始元素发布之前的延迟时间。

    - intervalMillis(long period)和 intervalMillis(long delay, long period)：与 interval()方法的作用相同，只不过该方法通过毫秒数来指定时间间隔和延迟时间。

    - 栗子

      ![img](https://img.mubu.com/document_image/fd548af4-efa3-4c19-acb9-f49d767c7e55-172511.jpg)

- generate() 方法

    - generate() 方法通过同步和逐一的方式来产生 Flux 序列。

    - 序列的产生是通过调用所提供的 SynchronousSink 对象的 next()，complete()和 error(Throwable)方法来完成的。

    - 逐一生成的含义是在具体的生成逻辑中，next() 方法只能最多被调用一次。

    - 在进行序列生成时，状态对象会作为 generator 使用的第一个参数传入，可以在对应的逻辑中对该状态对象进行修改以供下一次生成时使用。

    - 栗子

      ![img](https://img.mubu.com/document_image/87981015-a9d6-4d8f-a4cd-bbde50c5e09f-172511.jpg)

- create()方法

    - create()方法与 generate()方法的不同之处在于所使用的是 FluxSink 对象。

    - FluxSink 支持同步和异步的消息产生，并且可以在一次调用中产生多个元素。

    - 栗子

      ![img](https://img.mubu.com/document_image/f5fd1f8a-2a23-4242-a89f-6c5c0e151b1e-172511.jpg)

### 创建 Mono

- fromCallable()、fromCompletionStage()、fromFuture()、fromRunnable()和 fromSupplier()：分别从 Callable、CompletionStage、CompletableFuture、Runnable 和 Supplier 中创建 Mono。

- delay(Duration duration)和 delayMillis(long duration)：创建一个 Mono 序列，在指定的延迟时间之后，产生数字 0 作为唯一值。

- ignoreElements(Publisher source)：创建一个 Mono 序列，忽略作为源的 Publisher 中的所有元素，只产生结束消息。

- justOrEmpty(Optional<? extends T> data)和 justOrEmpty(T data)：从一个 Optional 对象或可能为 null 的对象中创建 Mono。只有 Optional 对象中包含值或对象不为 null 时，Mono 序列才产生对应的元素。

- 栗子

  ![img](https://img.mubu.com/document_image/9a4e45b4-b6be-4526-962e-6101801594f4-172511.jpg)

## 操作符

- buffer 和 bufferTimeout

    - 把当前流中的元素收集到集合中，并把集合对象作为流中的新元素。

    - 在进行收集时可以指定不同的条件：所包含的元素的最大数量或收集的时间间隔。

    - 方法 buffer()仅使用一个条件，而 bufferTimeout()可以同时指定两个条件。

    - 指定时间间隔时可以使用 Duration 对象或毫秒数，即使用 bufferMillis()或 bufferTimeoutMillis()两个方法。

    - bufferUntil 会一直收集直到 Predicate 返回为 true。使得 Predicate 返回 true 的那个元素可以选择添加到当前集合或下一个集合中；

    - bufferWhile 则只有当 Predicate 返回 true 时才会收集。一旦值为 false，会立即开始下一次收集。

    - 栗子

      ![img](https://img.mubu.com/document_image/6e9e4b92-c3f9-4643-96e8-624969638458-172511.jpg)

- filter

    - 对流中包含的元素进行过滤，只留下满足 Predicate 指定条件的元素。

    - 栗子

      ![img](https://img.mubu.com/document_image/38791913-8709-4c33-8001-a03d72076720-172511.jpg)

- window

    - window 操作符的作用类似于 buffer，所不同的是 window 操作符是把当前流中的元素收集到另外的 Flux 序列中，因此返回值类型是 Flux<flux>。

    - 栗子

      ![img](https://img.mubu.com/document_image/099d0f8e-d455-4b9a-ae70-226709aa7bd3-172511.jpg)

- zipWith

    - zipWith 操作符把当前流中的元素与另外一个流中的元素按照一对一的方式进行合并。在合并时可以不做任何处理，由此得到的是一个元素类型为 Tuple2 的流；

    - 通过一个 BiFunction 函数对合并的元素进行处理，所得到的流的元素类型为该函数的返回值。

    - 栗子

      ![img](https://img.mubu.com/document_image/ee9fab83-52ce-4dc6-91d8-1b8d9158b81c-172511.jpg)

- take

    - take 系列操作符用来从当前流中提取元素。

    - take(long n)，take(Duration timespan)和 takeMillis(long timespan)：按照指定的数量或时间间隔来提取。

    - takeLast(long n)：提取流中的最后 N 个元素。

    - takeUntil(Predicate<? super T> predicate)：提取元素直到 Predicate 返回 true。

    - takeWhile(Predicate<? super T> continuePredicate)： 当 Predicate 返回 true 时才进行提取。

    - takeUntilOther(Publisher<?> other)：提取元素直到另外一个流开始产生元素。

    - 栗子

      ![img](https://img.mubu.com/document_image/069fcddc-ce2d-4b0e-bf1f-427350a64cd6-172511.jpg)

- reduce 和 reduceWith

    - reduce 和 reduceWith 操作符对流中包含的所有元素进行累积操作，得到一个包含计算结果的 Mono 序列。

    - 累积操作是通过一个 BiFunction 来表示的。在操作时可以指定一个初始值。如果没有初始值，则序列的第一个元素作为初始值。

    - 栗子

      ![img](https://img.mubu.com/document_image/a82f31e0-822a-4dfe-ba7a-0e2d184198eb-172511.jpg)

- merge 和 mergeSequential

    - merge 和 mergeSequential 操作符用来把多个流合并成一个 Flux 序列。

    - 不同之处在于 merge 按照所有流中元素的实际产生顺序来合并，而 mergeSequential 则按照所有流被订阅的顺序，以流为单位进行合并。

    - 栗子

      ![img](https://img.mubu.com/document_image/c955b6ee-c804-45c0-80cb-b3c59674d18c-172511.jpg)

- flatMap 和 flatMapSequential

    - flatMap 和 flatMapSequential 操作符把流中的每个元素转换成一个流，再把所有流中的元素进行合并。

    - 栗子

      ![img](https://img.mubu.com/document_image/96c25745-4833-4e0e-bc5b-e8eb87b5043c-172511.jpg)

- concatMap

    - concatMap 操作符的作用也是把流中的每个元素转换成一个流，再把所有流进行合并。

    - 与 flatMap 不同的是，concatMap 会根据原始流中的元素顺序依次把转换之后的流进行合并；

    - 与 flatMapSequential 不同的是，concatMap 对转换之后的流的订阅是动态进行的，而 flatMapSequential 在合并之前就已经订阅了所有的流。

    - 栗子

      ![img](https://img.mubu.com/document_image/3fb0588f-1067-4356-a935-357281ded5b4-172511.jpg)

- combineLatest

    - combineLatest 操作符把所有流中的最新产生的元素合并成一个新的元素，作为返回结果流中的元素。

    - 只要其中任何一个流中产生了新的元素，合并操作就会被执行一次，结果流中就会产生新的元素。

    - 栗子

      ![img](https://img.mubu.com/document_image/371a8303-f0a9-4f7a-acc4-b1a54b9daf1c-172511.jpg)

## 消息处理

- 当需要处理 Flux 或 Mono 中的消息时，可以通过 subscribe 方法来添加相应的订阅逻辑。

- 在调用 subscribe 方法时可以指定需要处理的消息类型，可以只处理其中包含的正常消息，也可以同时处理错误消息和完成消息。

- 栗子

    - 处理正常和错误信息

      ![img](https://img.mubu.com/document_image/f1074f0a-bd6f-483d-b398-ceaf13e08f1f-172511.jpg)

    - 出现错误时返回默认值

      ![img](https://img.mubu.com/document_image/fb123f7d-0a2c-4eeb-9103-54195281e064-172511.jpg)

    - 出现错误时根据异常类型来选择流

      ![img](https://img.mubu.com/document_image/09824ccf-6c44-4548-9bb1-8cf26f12ff03-172511.jpg)

    - 使用 retry 重试

      ![img](https://img.mubu.com/document_image/9d826713-6ab6-4c67-b8de-8310dd877c8b-172511.jpg)

## 调度器

- 不同调度器实现

    - 当前线程，通过 Schedulers.immediate()方法来创建。
    - 单一的可复用的线程，通过 Schedulers.single()方法来创建。
    - 使用弹性的线程池，通过 Schedulers.elastic()方法来创建。
        - 线程池中的线程是可以复用的。当所需要时，新的线程会被创建。
        - 如果一个线程闲置太长时间，则会被销毁。
        - 该调度器适用于 I/O 操作相关的流的处理。
    - 使用对并行操作优化的线程池，通过 Schedulers.parallel()方法来创建。
        - 其中的线程数量取决于 CPU 的核的数量。该调度器适用于计算密集型的流的处理。
    - 使用支持任务调度的调度器，通过 Schedulers.timer()方法来创建。
    - 从已有的 ExecutorService 对象中创建调度器，通过 Schedulers.fromExecutorService()方法来创建。

- 栗子

  ![img](https://img.mubu.com/document_image/045e542a-5bed-466e-8a1c-9bcff2d9c55f-172511.jpg)

## 测试

### 使用 StepVerifier

- StepVerifier 的作用是可以对序列中包含的元素进行逐一验证。

- 通过 StepVerifier.create()方法对一个流进行包装之后再进行验证。

- expectNext()方法用来声明测试时所期待的流中的下一个元素的值，而 verifyComplete()方法则验证流是否正常结束。

- 还有 verifyError()来验证流由于错误而终止。

- 栗子

  ![img](https://img.mubu.com/document_image/22766563-c9da-497f-bf06-30d484fde242-172511.jpg)

### 操作测试时间

- 通过 StepVerifier.withVirtualTime()方法可以创建出使用虚拟时钟的 StepVerifier。通过 thenAwait(Duration)方法可以让虚拟时钟前进。

- 栗子

  ![img](https://img.mubu.com/document_image/3fce296f-398b-4db9-8f39-62753640b674-172511.jpg)

  需要验证的流中包含两个产生间隔为一天的元素，并且第一个元素的产生延迟是 4 个小时。在通过 StepVerifier.withVirtualTime()方法包装流之后，expectNoEvent()方法用来验证在 4 个小时之内没有任何消息产生，然后验证第一个元素 0 产生；接着 thenAwait()方法来让虚拟时钟前进一天，然后验证第二个元素 1 产生；最后验证流正常结束。

### 使用 TestPublisher

- 可以控制流中元素的产生，甚至是违反反应流规范的情况。

- 栗子

  ![img](https://img.mubu.com/document_image/a71c6ee4-68b1-4b92-8911-9337cca92f02-172511.jpg)

### 调试

- 启动调试模式

    - 在调试模式启用之后，所有的操作符在执行时都会保存额外的与执行链相关的信息。

    - 当出现错误时，这些信息会被作为异常堆栈信息的一部分输出。通过这些信息可以分析出具体是在哪个操作符的执行中出现了问题。

    - 栗子

      ![img](https://img.mubu.com/document_image/c6d4713a-9176-47a5-a9fe-856702e5c2fd-172511.jpg)

- 使用检查点

    - 通过 checkpoint 操作符来对特定的流处理链来启用调试模式。

    - 当出现错误时，检查点名称会出现在异常堆栈信息中。

    - 栗子

      ![img](https://img.mubu.com/document_image/e1fda0ee-e9bf-4ace-a2f8-c4beb292b131-172511.jpg)

### 日志记录

- 在开发和调试中的另外一项实用功能是把流相关的事件记录在日志中。这可以通过添加 log 操作符来实现。

- 栗子

  ![img](https://img.mubu.com/document_image/f39c8c87-1768-4b2e-9bb4-35922a5f6c60-172511.jpg)

## 冷与热序列

- 冷序列的含义是不论订阅者在何时订阅该序列，总是能收到序列中产生的全部消息。

- 与之对应的热序列，则是在持续不断地产生消息，订阅者只能获取到在其订阅之后产生的消息。

- 栗子

  ![img](https://img.mubu.com/document_image/adacad9d-cc3d-4f06-b55f-1b0355b08d16-172511.jpg)