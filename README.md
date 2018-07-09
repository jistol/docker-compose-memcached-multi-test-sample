Memcached를 이용하여 Consistent Hashing이 어떻게 동작하는지 확인 할 수 있는 샘플 프로젝트 입니다.     
Key에 대한 분배및 Proxy 역활은 소스에 포함되어 있는 `Simple-Spring-Memcached(ssm)`에서 담당하게되며 git을 설치 후 clone 명령을 이용하여 다운받을 수 있습니다.       

```vim
$ git clone https://github.com/jistol/docker-compose-memcached-multi-test-sample.git    
```

## 구성 ##

- Gradle
- Spring Boot 
- Google Simple Spring Memcached (SSM)

## Memcached 실행 ##

1. Docker 설치    
    
2. Memcached Image 다운로드
- docker pull memcached    
     
3. docker-compose를 이용하여 실행    
- ${PROJECT_HOME}/docker-compose up -d       
- 위와 같이 실행하면 아래와 같이 3개의 포트로 memcached 서버가 기동됩니다.        

```vim
$ docker-compose up -d    
Creating network "memcached_default" with the default driver    
Creating memcached_memcached3_1 ... done    
Creating memcached_memcached1_1 ... done    
Creating memcached_memcached2_1 ... done    
$ docker ps -a    
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS                      NAMES     
8158e741dacb        memcached           "docker-entrypoint.s…"   13 seconds ago      Up 10 seconds               0.0.0.0:11212->11211/tcp   memcached_memcached2_1    
2627e168914e        memcached           "docker-entrypoint.s…"   13 seconds ago      Up 9 seconds                0.0.0.0:11211->11211/tcp   memcached_memcached1_1    
4c6daf4fc275        memcached           "docker-entrypoint.s…"   13 seconds ago      Up 9 seconds                0.0.0.0:11213->11211/tcp   memcached_memcached3_1    
```

## Test ##

아래와 같이 2가지 테스트를 할 수 있도록 구성되어 있습니다.        

### WAS 1 + Memcached 3 : Consistent Hashing 테스트 ###    

1. gradle 명령을 이용하여 Tomcat 기동합니다.        

```vim
$ gradle clean build -x bootRun    
```

2. 아래 URL을 통해 캐시를 주입합니다.    

```text
POST : http://localhost:8080/{key}/{value}
```

3. telnet으로 Memcached에 접속, 값이 들어갔는지 확인합니다.   

```vim
$ telnet localhost 11211
Trying ::1...
Connected to localhost.
Escape character is '^]'.
get {key}


$ telnet localhost 11212
Trying ::1...
Connected to localhost.
Escape character is '^]'.
get {key}

$ telnet localhost 11213
Trying ::1...
Connected to localhost.
Escape character is '^]'.
get {key}
```

2~3 과정을 반복하면 입력한 KEY값이 동일한 노드의 Memcached에 들어가는것을 볼 수 있습니다.    

### WAS 3 + Memcached 3 : 서버등록 순서 오류에 의한 키배분 오류 테스트 ###  

`resource/application.yml` 파일을 보면 아래와 같이 각 profiles마다 다르게 서버 순서를 나열해 두었습니다.    

```yaml
server.port: 8080
mem-server: localhost:11211 localhost:11212 localhost:11213

---
spring.profiles: local1
server.port: 8081
mem-server: localhost:11212 localhost:11213 localhost:11211

---
spring.profiles: local2
server.port: 8082
mem-server: localhost:11213 localhost:11211 localhost:11212
```

이와 같이 설정시 인입된 서버 port에 따라 동일한 key에 대해 ssm이 다르게 분배하는 것을 확인 하는 테스트입니다.    
 
1. gradle 명령을 이용하여 각 profile별로 Tomcat 3대를 기동합니다.        

```vim
$ gradle clean build -x bootRun    
$ gradle clean build -x bootRun -Dspring.profiles.active=local1   
$ gradle clean build -x bootRun -Dspring.profiles.active=local2   
```

2. 아래 URL을 통해 캐시를 주입합니다.    

```text
POST : http://localhost:8080/{key}/{value}
POST : http://localhost:8081/{key}/{value}
POST : http://localhost:8082/{key}/{value}
```

3. telnet으로 Memcached에 접속, 값이 들어갔는지 확인합니다.   

```vim
$ telnet localhost 11211
Trying ::1...
Connected to localhost.
Escape character is '^]'.
get {key}


$ telnet localhost 11212
Trying ::1...
Connected to localhost.
Escape character is '^]'.
get {key}

$ telnet localhost 11213
Trying ::1...
Connected to localhost.
Escape character is '^]'.
get {key}
```

2~3번 과정을 반복하면 동일한 key를 넣더라도 어느 port에 접근하여 넣었느냐에 따라 위치가 뒤 섞여있는 것을 확인할 수 있습니다.    