## 과제를 진행하며 느낀 점

이번 과제를 진행하면서 문뜩 null을 다루는데 있어서 자바가 아쉽다는 생각도 했고,
물론 현업에서 다루는 문제보다는 낮은 수준에서의 문제지만
null을 다루는 방식이 번거로워서 대학교에서 배웠던 코틀린이 문뜩 그리워지는 순간들이 있었습니다.

### Java의 null 처리
nullable 파라미터를 받는 메서드에서는 매번 null 체크 로직을 작성해야 했습니다...
특히 엔티티 업데이트 로직에서 부분 수정을 구현할 때,
각 필드마다 null 체크를 해야 하는 부분이 코드를 장황하게 만들었습니다.
```java
public void update(String title, LocalDate date, LocalTime time, String location) {
    if (title != null) {
        this.title = title;
    }
    if (date != null) {
        this.date = date;
    }
}
```

### Kotlin이였다면?
Kotlin의 nullable 타입과 엘비스 연산자를 사용했다면 훨씬 간결했을거 같다는 생각도 했습니다.
```kotlin
fun update(title: String?, date: LocalDate?, time: LocalTime?, location: String?) {
    title?.let { this.title = it }
    date?.let { this.date = it }
    time?.let { this.time = it }
    location?.let { this.location = it }
}

// 또는
this.title = title ?: this.title
```
더 많은 장점도 있고 단점도 있겠지만, null을 다루는데 있어서 자유로운 코틀린과,
null을 굉장히 엄격히 대하는 자바의 차이도 느껴짐과 동시에
요즘 기업들이 코프링을 사용하는데는 이런 이유도 한 몫하지 않을까? 하는 생각도 했습니다.

### 단위 테스트 작성 과정에서

테스트 코드를 작성하면서도 이런 차이가 느껴졌는데,

Mockito에서 `anyLong()` vs `any()`의 차이 때문에 발생한 null 관련 오류나,

ReflectionTestUtils로 ID를 주입해야 하는 불편함도 있었습니다.
```java
// Member.getId()가 null을 반환하는 문제
given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
    Member member = invocation.getArgument(0);
    ReflectionTestUtils.setField(member, "id", MEMBER_ID);  // 이런 처리가 필요함
    return member;
});
```

이번 과제에서 크게 느껴지진 않았지만 문뜩 지나가는 생각이여서 남겨봤습니다.
앞으로 프로젝트에서 코프링을 사용해볼 기회가 있다면,
이런 null 처리 부분에서 어떤 차이가 있는지 직접 경험해보고 싶다는 생각을 했습니다.