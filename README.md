# hiresort

 
 # 서비스 시나리오

기능적 요구사항
1. 고객이 휴양소를 예약(Reservation)한다.
2. 고객이 지불(Pay)한다.
3. 결제모듈(payment)에 결제를 진행하게 되고 '지불'처리 된다.
4. 결제 '승인' 처리가 되면 예약 확정(Issue)이 된다.
5. 고객은 마이페이지를 통해 진행상태(view)를 확인할 수 있다.
6. 고객이 취소(Cancel)하는 경우 지불 및 예약이 취소가 된다.

비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않으면, 예약은 받아지지 않아야 한다. - Sync 방식
2. 장애격리
    1. 결제가 수행되지 않더라도 주문 취소은 지속적으로 받을 수 있어야 한다.  - Async(event-driven)
    2. 결제 시스템이 과중되면 주문(Reservation)을 잠시 후 처리하도록 유도한다.  - Circuit breaker, fallback
3. 성능
    1. 마이페이지에서 주문상태(View) 확인한다. - CQRS

# 분석/설계


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과: http://www.msaez.io/#/storming/P3HDhaDCvERl1kR9ZDeRJxSKcBj1/4a13816c84aa49e8fcb8be6d90a94d15

### 이벤트 도출
![image](https://user-images.githubusercontent.com/79756040/132449863-765d51cc-3a08-4dd7-98f6-baba1ed5487b.png)

### 부적격 이벤트 탈락
![image](https://user-images.githubusercontent.com/79756040/132450003-845362f6-1879-421e-a587-37f5334737da.png)

### 완성된 최종 모형 ( 시나리오 점검 후 )
![image](https://user-images.githubusercontent.com/79756040/132453240-dfe4c95f-d2b4-45cb-99b1-4578ec5e5269.png)


## 헥사고날 아키텍처 다이어그램 도출 
![image](https://user-images.githubusercontent.com/79756040/132451181-58b58328-6dd9-4639-a9c2-12e2f161cb3d.png)
 
 # 구현
 
 분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 
 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8084, 8088 이다)

```
   cd reservation
   mvn spring-boot:run

   cd payment
   mvn spring-boot:run

   cd issue
   mvn spring-boot:run

   cd view
   mvn spring-boot:run

   cd gateway
   mvn spring-boot:run
  ```
  
# DDD(Domain Driven Design) 의 적용

- msaez.io 를 통해 구현한 Aggregate 단위로 Entity 를 선언 후, 구현을 진행하였다.
 Entity Pattern 과 Repository Pattern 을 적용하기 위해 Spring Data REST 의 RestRepository 를 적용하였다.

 ### Reservation 서비스의 Reserve.java 

```javapackage hiresort;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Date date;
    private Long persons;
    private Long rooms;
    private String region;
    private String resort;
    private String needs;
    private String reservationstatus;
    private Long issuedid;
    private String issuedstatus;
    private Long paymentid;
    private String paymenttype;
    private Long cost;

    @PostPersist
    public void onPostPersist(){
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        // reserved.publishAfterCommit();

        // reserved.setReservationstatus("Reservation Completed");

        hiresort.external.Payment payment = new hiresort.external.Payment();
        // mappings goes here

        payment.setReservationid(this.getId());
        payment.setPaymenttype(this.getPaymenttype());
        payment.setPaymentstatus("Not Pay");
        payment.setRegion(this.getRegion());
        payment.setResort(this.getResort());
        payment.setCost(this.getCost());
        payment.setPersons(this.getPersons());
        payment.setRooms(this.getRooms());       

        // ReservationApplication.applicationContext.getBean(hiresort.external.PaymentService.class)
        //     .Pay(this.getPaymentid());

        ReservationApplication.applicationContext.getBean(hiresort.external.PaymentService.class)
        .paid(payment);

        reserved.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
   
        // reserved.publishAfterCommit();
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.


    }

    @PostRemove
    public void onPostRemove(){
        ReservationCancelled reservationCancelled = new ReservationCancelled();
        BeanUtils.copyProperties(this, reservationCancelled);
        reservationCancelled.setReservationstatus("Reservation Cancelled");
        reservationCancelled.publishAfterCommit();

        // hiresort.external.Payment payment = new hiresort.external.Payment();
        // ReservationApplication.applicationContext.getBean(hiresort.external.PaymentService.class)
        //     .paycancel(this.getPaymentid());

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Long getPersons() {
        return persons;
    }

    public void setPersons(Long persons) {
        this.persons = persons;
    }
    public Long getRooms() {
        return rooms;
    }

    public void setRooms(Long rooms) {
        this.rooms = rooms;
    }
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    public String getResort() {
        return resort;
    }

    public void setResort(String resort) {
        this.resort = resort;
    }
    public String getNeeds() {
        return needs;
    }

    public void setNeeds(String needs) {
        this.needs = needs;
    }
    public String getReservationstatus() {
        return reservationstatus;
    }

    public void setReservationstatus(String reservationstatus) {
        this.reservationstatus = reservationstatus;
    }

    public String getIssuedstatus() {
        return issuedstatus;
    }
    public void setIssuedstatus(String issuedstatus) {
        this.issuedstatus = issuedstatus;
    }
    public Long getIssuedid() {
        return issuedid;
    }
    public void setIssuedid(Long issuedid) {
        this.issuedid = issuedid;
    }

    public long getCost() {
        return cost;
    }


    public void setCost(long cost) {
        this.cost = cost;
    }


    public String getPaymenttype() {
        return paymenttype;
    }


    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    public Long getPaymentid() {
        return paymentid;
    }


    public void setPaymentid(Long paymentid) {
        this.paymentid = paymentid;
    }
}
```

### Payment 시스템의 PolicyHandler.java

```java
package hiresort;

import hiresort.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyHandler{
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCancelled_Paycancel(@Payload ReservationCancelled reservationCancelled){

        // if(!reservationCancelled.validate()) return;

        // System.out.println("\n\n##### listener Paycancel : " + reservationCancelled.toJson() + "\n\n");

        // // Sample Logic //
        // Payment payment = new Payment();
        // payment.setReservationid(reservationCancelled.getId());
        // // payment.setPaymenttype(reservationCancelled.getPaymenttype());
        // payment.setPaymentstatus("Pay Cancelled");
        // payment.setRegion(reservationCancelled.getRegion());
        // payment.setResort(reservationCancelled.getResort());
        // // payment.setCost(reservationCancelled.getCost());
        // payment.setPersons(reservationCancelled.getPersons());
        // payment.setRooms(reservationCancelled.getRooms());       
        // paymentRepository.save(payment);
        if(reservationCancelled.validate())
        {
            System.out.println("\n\n##### listener PayCancel : " + reservationCancelled.toJson() + "\n\n");
            List<Payment> paymanetsList = paymentRepository.findByreservationid(reservationCancelled.getId());
            if(paymanetsList.size()>0) {
                for(Payment payment : paymanetsList) {
                    if(payment.getReservationid().equals(reservationCancelled.getId())){
                        System.out.println("##### OrderId :: "+ payment.getId() 
                                                      +" ... "+ payment.getResort()+" is Cancelled");
                        payment.setPaymenttype("ORDER CANCEL");
                        paymentRepository.save(payment);
                    }
                }
            }
        }


    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
```

적용 후 REST API 테스트를 통해 정상 동작 확인할 수 있다.
- 예약(Reserved)
![image](https://user-images.githubusercontent.com/79756040/132451644-d6c79800-cfbf-4a36-816c-0d0155303547.png)
- 지급(Payment)
![image](https://user-images.githubusercontent.com/79756040/132451631-a2505585-c3aa-433a-b808-f715d0264b76.png)
- 예약확정(Issue)
![image](https://user-images.githubusercontent.com/79756040/132451661-4a35a6e6-6bdc-4c99-a8da-a583b79992d0.png)

- 주문취소(Reserve Cancel)
![image](https://user-images.githubusercontent.com/79756040/132452061-79127fde-ab8a-4bb3-9542-914ceb23cab2.png)
- 지불취소(Cancel Payment)
![image](https://user-images.githubusercontent.com/79756040/132452113-930b0537-90b8-42c9-833f-b1b974f658da.png)
- 


# Gateway 적용
API Gateway를 통하여 마이크로 서비스들의 진입점을 통일하였다.

server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: reservation
          uri: http://localhost:8081
          predicates:
            - Path=/reservations/** 
        - id: payment
          uri: http://localhost:8082
          predicates:
            - Path=/payments/** 
        - id: Issue
          uri: http://localhost:8083
          predicates:
            - Path=/issueds/** 
        - id: view
          uri: http://localhost:8084
          predicates:
            - Path= /views/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: reservation
          uri: http://reservation:8080
          predicates:
            - Path=/reservations/** 
        - id: payment
          uri: http://payment:8080
          predicates:
            - Path=/payments/** 
        - id: Issue
          uri: http://Issue:8080
          predicates:
            - Path=/issueds/** 
        - id: view
          uri: http://view:8080
          predicates:
            - Path= /views/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080
  
  ```

# 폴리그랏 퍼시스턴스
- issue 서비스의 경우, 다른 마이크로 서비스와 달리 hsql을 구현하였다.
- 이를 통해 서비스 간 다른 종류의 데이터베이스를 사용하여도 문제 없이 동작하여 폴리그랏 퍼시스턴스를 충족함.
### delivery 서비스의 pom.xml
![image](https://user-images.githubusercontent.com/79756040/132451827-8278e925-71db-4e15-a2b5-2dcfc996501d.png)

# 동기식 호출(Req/Res 방식)과 Fallback 처리
- reservation 서비스의 external/PaymentService.java 내에 결제(paid) 서비스를 호출하기 위하여 
FeignClient를 이용하여 Service 대행 인터페이스(Proxy)를 구현

### order/external/PaymentService.java
![image](https://user-images.githubusercontent.com/79756040/132451947-a2ecc9a7-05f2-4e95-baf8-dde46e14a7a9.png)

### Order 서비스의 Order.java
### 이하 생략
```

- payment서비스를 내림

![image](https://user-images.githubusercontent.com/86760678/130350522-9f175e77-47f6-43c9-84bb-2c08fadd8525.png)

- 주문(order) 요청 및 에러 난 화면 표시

![image](https://user-images.githubusercontent.com/86760678/130350564-86e59bda-2b77-44ee-b872-b5939634ba8b.png)

- payment 서비스 재기동 후 다시 주문 요청

![image](https://user-images.githubusercontent.com/86760678/130350697-2ca7b817-36d6-4f33-80b7-be19a1f4ce7a.png)

- payment 서비스에 주문 대기 상태로 저장 확인

![image](https://user-images.githubusercontent.com/86760678/130350742-87a88566-aad3-41e8-a72e-96cce39fa9c5.png)


# 비동기식 호출(Pub/Sub 방식)
- reserve 서버스 내 Order.java에서 아래와 같이 서비스 Pub 구현

```java

///

@Entity
@Table(name="Order_table")
public class Order {

///
    @PostRemove
    public void onPostRemove(){
        OrderCancelled orderCancelled = new OrderCancelled();
        BeanUtils.copyProperties(this, orderCancelled);
        orderCancelled.setOrderStatus("Order Cancelled");
        orderCancelled.publishAfterCommit();
    }
///

```

- payment 서비스 내 PolicyHandler.java에서 아래와 같이 Sub 구현

```java
///

@Service
public class PolicyHandler{
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_PayCancel(@Payload OrderCancelled orderCancelled)
    {
        if(orderCancelled.validate())
        {
            System.out.println("\n\n##### listener PayCancel : " + orderCancelled.toJson() + "\n\n");
            List<Payment> paymanetsList = paymentRepository.findByOrderId(orderCancelled.getId());
            if(paymanetsList.size()>0) {
                for(Payment payment : paymanetsList) {
                    if(payment.getOrderId().equals(orderCancelled.getId())){
                        System.out.println("##### OrderId :: "+ payment.getId() 
                                                      +" ... "+ payment.getProductName()+" is Cancelled");
                        payment.setPaymentType("ORDER CANCEL");
                        paymentRepository.save(payment);
                    }
                }
            }
        }
    }
///
}

```

- 비동기식 호출은 다른 서비스가 비정상이여도 이상없이 동작가능하여, payment 서비스에 장애가 나도 order 서비스는 정상 동작을 확인

### payment 서비스 내림

![image](https://user-images.githubusercontent.com/86760678/130352224-7d22d74d-4ebf-4ac5-91b0-b36092219ca4.png)

### 주문 취소

![image](https://user-images.githubusercontent.com/86760678/130352256-ff8aa934-f49c-4f38-a3a8-3774c05fc956.png)



# CQRS

viewer 인 ordertraces 서비스를 별도로 구현하여 아래와 같이 view가 출력된다.

### 주문 수행 후, ordertraces

![image](https://user-images.githubusercontent.com/86760678/130352429-83e1a1d3-e263-47d7-9760-becfccf9cc96.png)

![image](https://user-images.githubusercontent.com/86760678/130352435-18c4912e-11d7-4368-b0b5-0a8568bc740d.png)

### 주문 취소 수행 후, ordertraces

![image](https://user-images.githubusercontent.com/86760678/130352458-f2b7ad3e-4b00-4fb8-a06e-75e985475c53.png)



# 운영
  
## Deploy / Pipeline

- git에서 소스 가져오기

```
https://github.com/nastory-donghwan/hiresort.git
```

- Build 및 Azure Container Resistry(ACR) 에 Push 하기
 
```bash
cd ..
cd reservation
mvn package
az acr build --registry nastory --image nastory.azurecr.io/reservation:latest .

cd ..
cd payment
mvn package
az acr build --registry nastory --image nastory.azurecr.io/payment:latest .

cd ..
cd issue
mvn package
az acr build --registry nastory --image nastory.azurecr.io/issue:latest .

cd ..
cd view
mvn package
az acr build --registry nastory --image nastory.azurecr.io/view:latest .

cd ..
cd gateway
mvn package
az acr build --registry nastory --image nastory.azurecr.io/gateway:latest .

```

- ACR에 정상 Push 완료

![image](https://user-images.githubusercontent.com/79756040/132453826-635c5094-f73b-40e7-bca2-5e8f3e0dbdbe.png)


- Kafka 설치 및 배포 완료

![image](https://user-images.githubusercontent.com/79756040/132453845-b93a9b4b-fa42-464b-806e-72b2b155482c.png)

- Kubernetes Deployment, Service 생성

```sh
cd ..
cd reservation/kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

cd ..
cd payment/kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

cd ..
cd issue/kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

cd ..
cd view/kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

cd ..
cd gateway/kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml
```

/reservation/kubernetes/deployment.yml 파일 
![image](https://user-images.githubusercontent.com/79756040/132453951-5a418ce5-e5b2-4290-8c6c-58534ff18e7e.png)


/order/kubernetes/service.yaml 파일 
![image](https://user-images.githubusercontent.com/79756040/132453964-d9b7f8f6-4a34-4005-9a05-c61a79c72046.png)


전체 deploy 완료 현황

![image](https://user-images.githubusercontent.com/89397401/130646019-c0a9e001-3433-44da-8acd-9a1637a4b2e9.png)

## Persistence Volume

- 비정형 데이터를 관리하기 위해 PVC 생성 파일

pvc.yml
- AccessModes: **ReadWriteMany**
- storeageClass: **azurefile**
```yml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: order-disk
spec:
  accessModes:
  - ReadWriteMany
  storageClassName: azurefile
  resources:
    requests:
      storage: 1Gi
```
deploymeny.yml
```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order
  labels:
    app: order
spec:
  replicas: 1
  -- 생략 --
  template:
  -- 생략 --
    spec:
      containers:
        - name: order
        -- 생략 --
          volumeMounts:
            - name: volume
              mountPath: "/mnt/azure"
      volumes:
      - name: volume
        persistentVolumeClaim:
          claimName: order-disk
```
application.yml
```yml
logging:
  level:
    root: info
  file: /mnt/azure/logs/order.log
```
- 로그 확인

![PVC-LOGS](https://user-images.githubusercontent.com/89397401/130727058-c8b4e2d1-b07e-4b4f-9fdb-9acfe25f5943.png)


## Autoscale (HPA:HorizontalPodAutoscaler)

- 특정 수치 이상으로 사용자 요청이 증가할 경우 안정적으로 운영 할 수 있도록 HPA를 설치한다.

order 서비스에 resource 사용량을 정의한다.

order/kubernetes/deployment.yml
```yml
  resources:
    requests:
      memory: "64Mi"
      cpu: "250m"
    limits:
      memory: "500Mi"
      cpu: "500m"
```

order 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15%를 넘어서면 replica를 10개까지 늘려준다.

```sh
kubectl autoscale deploy order --min=1 --max=10 --cpu-percent=15
```

![image](https://user-images.githubusercontent.com/89397401/130735894-d889090c-95c6-4fa7-a1b5-aa1311e11eb9.png)

siege를 활용하여, 부하 생성한다. (30명의 동시사용자가 30초간 부하 발생)

```sh
siege -c30 -t30S -v --content-type "application/json" 'http://order:8080/orders POST { "productId": 1, "qty": 2, "paymentType" : "card", "cost" : 2000, "productName" : "RedTea"}'
```

- Autoscale을 확인하기 위해 모니터링한다.

```sh
$ watch kubectl get all
```

- 결과 확인 : 부하 생성 이후 CPU 15% 이상 사용 시 자동으로 POD가 증가하면서 Autoscale 됨을 확인 할 수 있다.

![image](https://user-images.githubusercontent.com/89397401/130736630-9a4de0c5-82d6-416c-a24a-11253d8412f0.png)

## Circuit Breaker

- FeignClient + istio를 활용하여 Circuit Breaker 동작을 확인한다.
- istio 설치 후 istio injection이 enabled 된 namespace를 생성한다.
```
kubectl create namespace skanu
kubectl label namespace skanu istio-injection=enabled
```

- namespace label에 istio-injection이 enabled 된 것을 확인한다.  

![image](https://user-images.githubusercontent.com/89397401/130982037-c78b9dfe-328b-4712-b60d-d5954be8cab0.png)

- 해당 namespace에 기존 서비스들을 재배포한다.

- deploy 실행
```Bash
kubectl create deploy order --image skccacr.azurecr.io/order:latest -n skanu
kubectl create deploy payment --image skccacr.azurecr.io/payment:latest -n skanu
kubectl create deploy delivery --image skccacr.azurecr.io/delivery:latest -n skanu
kubectl create deploy gateway --image skccacr.azurecr.io/gateway:latest -n skanu
kubectl create deploy ordertrace --image skccacr.azurecr.io/ordertrace:latest -n skanu
kubectl get all -n skanu
```

- expose 하기
```Bash
kubectl expose deploy order --type="ClusterIP" --port=8080 -n skanu
kubectl expose deploy payment --type="ClusterIP" --port=8080 -n skanu
kubectl expose deploy delivery --type="ClusterIP" --port=8080 -n skanu
kubectl expose deploy gateway --type="LoadBalancer" --port=8080 -n skanu
kubectl expose deploy ordertrace --type="ClusterIP" --port=8080 -n skanu
kubectl get all -n skanu
```

- 서비스들이 정상적으로 배포되었고, Container가 2개씩 생성된 것을 확인한다. 

![image](https://user-images.githubusercontent.com/89397401/130979652-92c51535-35f0-4c98-b5fa-3ad9fb85941b.png)

- Circuit Breaker 설정을 위해 아래와 같은 Destination Rule을 생성한다.

- Pending Request가 많을수록 오랫동안 쌓인 요청은 Response Time이 증가하게 되므로, 적절한 대기 쓰레드 풀을 적용하기 위해 connection pool을 설정했다.
```yaml
  apiVersion: networking.istio.io/v1alpha3
  kind: DestinationRule
  metadata:
    name: dr-httpbin
    namespace: skanu
  spec:
    host: gateway
    trafficPolicy:
      connectionPool:
        http:
          http1MaxPendingRequests: 1
          maxRequestsPerConnection: 1
```

- 설정된 Destinationrule을 확인한다. 

![image](https://user-images.githubusercontent.com/89397401/130977740-9fb3cbe1-4207-48ca-a835-8f1b13fa7d64.png)

- siege 를 활용하여 User가 2명인 상황에 대해서 요청을 보낸다. (설정값 c2)
  - siege 는 같은 namespace 에 생성하고, 해당 pod 안에 들어가서 siege 요청을 실행한다.
```
kubectl exec -it (siege POD 이름) -c siege -n istio-test-ns -- /bin/bash

siege -c2 -t30S -v --content-type "application/json" 'http://EXTERNAL-IP:8080/orders POST {"productId": 1, "qty":3, "paymentTyp": "cash", "cost": 1000, "productName": "Coffee"}'
```

- 최종결과 확인
![image](https://user-images.githubusercontent.com/89397401/130823691-3df705c8-b964-4175-80c2-8970f4be57da.png)

- 운영시스템은 죽지 않고 지속적으로 Circuit Breaker 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 
- 약 84%정도 정상적으로 처리되었음.


## Zero-Downtime deploy (Readiness Probe)

- 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함
- 생성된 siege Pod 안쪽에서 정상작동 확인
```
kubectl exec -it siege -- /bin/bash
```

- siege 로 배포작업 직전에 워크로드를 모니터링 함
```
siege -c1 -t30S -v http://order:8080/orders
```

- Readiness가 설정되지 않은 yml 파일로 배포 진행
 
![image](https://user-images.githubusercontent.com/44763296/130768667-b3df9f46-d907-40aa-9304-4b0888a9983b.png)

```
kubectl apply -f deployment_without_readiness.yml
```

- 아래 그림과 같이, Kubernetes가 준비가 되지 않은 delivery pod에 요청을 보내서 siege의 Availability 가 100% 미만으로 떨어짐 

![image](https://user-images.githubusercontent.com/44763296/130476167-1b1eca10-ac7f-4065-86b7-69af9dcd7be5.png)


- 정지 재배포 여부 확인 전에, siege 로 배포작업 직전에 워크로드를 모니터링
```
siege -c1 -t60S -v http://order:8080/orders --delay=1S
```

- Readiness가 설정된 yml 파일로 배포 진행
```
readinessProbe:
  httpGet:
    path: '/actuator/health'
    port: 8080
  initialDelaySeconds: 10
  timeoutSeconds: 2
  periodSeconds: 5
  failureThreshold: 10
```

```
kubectl apply -f deployment_with_readiness.yml
```

- 배포 중 pod가 2개가 뜨고, 새롭게 띄운 pod가 준비될 때까지, 기존 pod가 유지됨을 확인
![image](https://user-images.githubusercontent.com/44763296/130477984-32b58cbf-a666-4e52-a377-d99c7a1477b7.png)
![image](https://user-images.githubusercontent.com/44763296/130478069-f3b1156e-bddb-48e3-ace9-ee21d3965206.png)


## Self-healing (Liveness Probe)

- order 서비스의 yml 파일에 liveness probe 설정을 바꾸어서, liveness probe 가 동작함을 확인

- liveness probe 옵션을 추가하되, 서비스 포트가 아닌 8090으로 설정, readiness probe 미적용
```
        livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8090
            initialDelaySeconds: 5
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
```

- order 서비스에 liveness가 적용된 것을 확인
```
kubectl describe po order
```
![image](https://user-images.githubusercontent.com/44763296/130482646-e598d23a-85b5-4e9d-b48b-6c9b99c7955f.png)


- order 에 liveness가 발동되었고, 8090 포트에 응답이 없기에 Restart가 발생함
![image](https://user-images.githubusercontent.com/44763296/130482675-ec20503f-a5d1-4c3d-9261-a89ae7ee17f1.png)
![image](https://user-images.githubusercontent.com/44763296/130482712-e18cda81-f3c8-47f7-abfc-47188cc423ad.png)


