# JPA-StartProject

본 프로젝트는 JPA + SpringBoot를 사용함에 있어서 학습한 내용들을 정리한 프로젝트
         (또한, JPA를 사용하기 위해 알아야 할 기초적인 내용까지 복습하면서 정리)

목차

### 1. JPA 환경 설정

---

- 프로젝트 환경 설정
    - 뷰: React.js
    - 웹 계층: 스프링 MVC
    - 데이터 저장 계층: JPA + Hibernate
    - 프레임워크 : Spring Framework (Spring Boot 2.4.2)
    - 빌드 : Gradle (Java 11.0.1)

- 프로젝트 요구 기능
    - 회원 기능(회원 등록, 회원 조회)
    - 상품 기능(상품 등록, 상품 수정, 상품 조회)
    - 주문 기능(상품 주문, 주문 내역 조회, 주문 취소)

### 2. JPA 도메인 설계

---

- Member

    ```java
    @Entity @Getter @Setter
    public class Member {
        @Id @GeneratedValue
        @Column(name = "member_id")
        private Long id;
        private String name;

        @Embedded
        private Address address;

        @OneToMany(mappedBy = "member")
        private List<Order> orders = new ArrayList<>();
    }
    ```

    ### ✏️ @Id, @GenerateValue란?

    - JPA에서는 기본 키 매핑은 @Id를 통해서 사용한다. 이때 두 가지 전략이 있는데 **직접 할당 vs 자동 생성** 으로 나뉘게 된다.

        기본키를 직접 할당하려면 @Id만 사용하면 되고, 자동 생성 전략을 사용하려면 @Id에 @GenerateValue까지 추가하고 원하는 키 생성 전략을 선택하면 된다.

        해당 프로젝트에서는 자동 생성 전략 중 **AUTO 전략을 사용한다!!** ( @GenerateValue의 기본값은 GenerationType.AUTO)

        IDENTITY는 DB에 INSERT가 되야 PK 값이 나온다!! 여기서 JPA와의 문제가 발생!!!!!

        - 딱, 이 IDENTITY 전략에서만 예외적으로 **DB에 em.persist를 호출한 시점에 DB 쿼리(INSERT) 를 날려버림!!!!!  그리고 JPA가 내부적으로 SELECT를 해서 PK 값을 가져온다.**

    ### ✏️ @Embedded란?

    - JPA에서는 데이터 타입을 가장 크게 분류하면 **엔티티 타입과 값 타입**으로 나눌 수 있다.

        엔티티 타입은 식별자를 통해 지속해서 추적 할 수 있지만, 값 타입은 식별자가 없고 숫자나 문자같은 속성만 있으므로 추적이 불가능하다. 

        **즉, 값 타입은 단순한 수치 정보이다.**

    - 객체지향적 설계시 '회원' 엔티티가 상세한 데이터를 가지고 있는 것은 **너무 응집력이 떨어지는 코드이다.** 즉, 이를 **임베디드 타입을 사용해서 해결해야 할 필요성**이 있다.

        ![JPA-StartProject%207da12e2a1ffe4eceb7bf5831015e043d/Untitled.png](JPA-StartProject%207da12e2a1ffe4eceb7bf5831015e043d/Untitled.png)

    위와 같이 정의시 재사용 뿐만 아니라 응집도를 높게 설계가 가능하다. 또한 **해당 값 타입만 사용하는 의미 있는 메소드도 만들 수 있다**.

    이런 임베디드 타입을 사용하기 위해서는 2가지의 어노테이션이 필요하다. 이것이 바로 설명하고자 한 **@Embedded, @Embeddable** 이다.

    - **@Embedded : 임베디드 타입을 사용하는 곳에 표시**
    - **@Embeddable : 임베디드 타입을 정의하는 곳에 표시**

    임베디드 타입 값은 값 타입을 여러 엔티티에서 공유하면 굉장히 위험하다. (재사용이 아닌 공유임!!!!!!)

    ```java
    member1.setHomeAddress(new Address("OldCity"));
    Address address = member1.getHomeAddress();

    address.setCity("NewCity"); // 회원1의 address값을 공유해서 사용
    member2.setHomeAddress(address);
    ```

    이렇게 되면 **회원 2의 주소 뿐만 아니라 회원 1도 같이 변경**이 되어 버린다!!

    - 해결책으로 2가지를 제시하는데 **값 타입 복사, 불변 객체**가 있다. 여기서 확실하게 side-effect를 제거하고자 **불변 객체(immutable object)**를 사용한다.
    - 사용법

        ```java
        @Enbeddable
        @Getter // 접근자만 허용
        public class Address {
        	private String city;
        	
        	protected Address() {} // JPA에서 기본 생성자는 필수!!!!

        	// 생성자로 초기 값을 설정
        	public Address(String city) {this.city = city}
        }

        Address address = new Address("city");

        Member member = new Member();
        member.setUsername("member1");
        member.setHomeAddress(address);
        em.persist(member);

        Address newAddress = new Address("Newcity");
        member.setHomeAddress(newAddress);

        ```

        - 즉, 아예 통채로 갈아 끼우는 식으로 설계를 해야 한다!!!!!

---

- Order (내용이 많아 축약)

    ```java
    @Entity @Table(name = "orders") @Getter @Setter
    public class Order {
    	@Id @GenerateValue @Column(name = "order_id")
    	private Long id;

    	@ManyToOne(fetch = FetchType.LAZY)
    	@JoinColumn(name = "member_id")
    	private Member member;

    	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    	private List<OrderItem> ordersItem = new ArrayList<>();

    	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.Lazy)
    	@JoinColumn(name = "delivery_id")
    	private Delivery delivery;

    	@Enumerated(EnumType.STRING)
    	private OrderStatus status; 
    }
    ```

    ### ✏️ 연관관계 매핑

    - N : 1 - 다대일
        - 항상 연관관계의 주인은 N쪽이다. 코드 작성법은 다음과 같다.

            ```java
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "//1쪽의 pk(member_id)")
            private Member member;
            ```

            ```java
            @OneToMany(mappedBy = "n쪽의 변수명(team)")
            private List<Member> members = new ArrayList<Member>();
            ```

            ⚠️ 주의사항 및 알아야 할 사항!!!

            반드시 'N'쪽에 작성되는 ManyToOne에는 (fetch = FetchType.LAZY)를 작성해줘야 한다.

    - 1 : 1 - 일대일 관계
        - 주 테이블이나 대상 테이블 중에 외래 키 선택 가능
            - **주 테이블에 외래 키를 설계하는 경우**

                외래 키를 객체 참조와 비슷하게 사용할 수 있어서 객체지향 개발자들이 선호한다. 이 방법의 장점은 주 테이블이 외래 키를 가지고 있으므로 **주 테이블만 확인해도 대상 테이블과 연관관계가 있는지 알 수 있다**.

            - 대상 테이블에 외래 키(단방향x, 양방향은 지원)

                데이터베이스 개발자들이 선호. 테이블 관계가 1:N으로 변경할 때 테이블 구조를 그대로 유지 할 수 있다. 

### 3. JPA 비즈니스 로직 설계