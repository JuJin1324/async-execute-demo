# async-execute-demo

## 설정
### Thread Bean 추가
> Spring Async 메서드에서 공통으로 사용할 Thread 설정 객체를 Bean 설정한다.
> ```java
> /* config/AsyncConfigure.java */
> @Bean(name = "threadPoolTaskExecutor")
> public Executor threadPoolTaskExecutor() {
>     ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
>     taskExecutor.setCorePoolSize(6);        /* 고정 스레스 갯수 */
>     taskExecutor.setMaxPoolSize(12);        /* 최대 스레스 갯수 */
>     taskExecutor.setThreadNamePrefix("taskExecutor-");  /* log 출력시 나올 이름 지정 */
>     taskExecutor.initialize();
>     return taskExecutor;
> }
> ```

### Async 메서드 추가
> 위에서 설정한 Thread 를 통해서 비동기 실행할 메서드 추가 - 이미 만들어놓은 Service 의 메서드 중에서 비동기로 실행시키고 싶은 메서드를 
> 다음과 같이 수정한다.  
> 
> 1. <b>@Async</b> 애노테이션 추가: 위에서 설정한 Thread Bean 객체의 이름을 괄호 안에 넣어준다.
> 2. 기존의 리턴 타입을 `Future<>`로 덮는다: 기존에 `String` 을 리턴했으면 `Future<String>` 으로, 기존에 `List<String>`을 리턴했으면 
> `Future<List<String>>` 으로 리턴을 수정한다.  
> 단 기존에 리턴이 void 였으면 Future 로 덮지 않고 그대로 void 를 사용한다.  
> 3. 리턴 타입을 변경함에 따라 리턴 값 또한 기존의 리턴 값을 `new AsyncResult<>()` 로 덮는다: 기존에 `return "hello!";` 로 리턴했으면 
> `return new AsyncResult<>("hello!");` 로 변경한다.  
>
> <b>주의</b>: 동일 클래스 내에서 @Async 메서드를 콜하면 비동기가 적용되지 않는다. 비동기로 해당 메서드를 콜 하고 싶으면 클래스가 다른 메서드에서 콜하여야 한다. 
>
> ```java
> /* 기존 동기 메서드를 가진 Service.java */
> @Async("threadPoolTaskExecutor")
> public Future<String> sayHelloAsync() {
>     try {
>         log.info("Method sayHello called in Async way.");
>         Thread.sleep((int)(Math.random()*10) * 1000);
>     } catch (InterruptedException e) {
>         log.error(e.getMessage());
>     }
>     return new AsyncResult<>("hello!");
> }
> ```

### 메서드 실행
> for 문을 통해서 위의 `AsyncExecutorService` 에서 @Async 애노테이션한 `sayHelloAsync` 메서드를 10번 비동기 호출하였다.   
> 내부에서 `Method sayHello called in Async way.` 로그를 먼저 출력한 후 0~9초를 랜덤으로 sleep 한 이후 `hello` 를 반환한다.  
> 
> 실행만 시키고 일단 받아온 Future 객체들을 모두 futures 배열에 추가 후 forEach 메서드를 통해서 각각의 Future 에 대해서 로직을 진행한다.
> 각 Future 가 끝날 때 까지 `stringFuture.isDone()`로 체크한다. 끝났으면 log 로 리턴 값(여기서는 `hello`)을 출력한다.
> ```java
> List<Future<String>> futures = new ArrayList<>();
> for (int i = 0; i < 10; i++) {
>     futures.add(asyncExecutorService.sayHelloAsync());
> }
> futures.forEach(stringFuture -> {
>     while (true) {
>         if (stringFuture.isDone()) {
>             try {
>                 log.info(stringFuture.get());
>             } catch (InterruptedException | ExecutionException e) {
>                 log.error(e.getMessage());
>             }
>             break;
>         }
>     }
> });
> ```
> 
> 결과
> ```bash
> 2020-11-30 16:04:32.148  INFO 74524 --- [ taskExecutor-1] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:32.148  INFO 74524 --- [ taskExecutor-6] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:32.148  INFO 74524 --- [ taskExecutor-2] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:32.148  INFO 74524 --- [ taskExecutor-5] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:32.148  INFO 74524 --- [ taskExecutor-3] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:32.148  INFO 74524 --- [ taskExecutor-4] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:32.149  INFO 74524 --- [ taskExecutor-2] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:34.150  INFO 74524 --- [ taskExecutor-6] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:34.150  INFO 74524 --- [ taskExecutor-4] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:34.150  INFO 74524 --- [ taskExecutor-5] p.d.j.a.service.AsyncExecutorService     : Method sayHello called in Async way.
> 2020-11-30 16:04:40.153  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.153  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.153  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.153  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.153  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.153  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.153  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.154  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.154  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> 2020-11-30 16:04:40.154  INFO 74524 --- [  restartedMain] p.d.jujin.async.runner.AsyncDemoRunner   : hello!
> ```