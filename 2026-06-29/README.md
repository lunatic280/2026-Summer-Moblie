# Kotlin 학습 정리 - 배열, 반복문, 컬렉션, 클래스

## 학습 날짜

2026년 6월 29일

## 학습 주제

오늘은 Kotlin의 기본 문법 중 배열과 반복문을 먼저 학습하고, 이후 컬렉션과 객체지향 문법까지 함께 정리했다.

주요 학습 내용은 다음과 같다.

- 배열 생성과 값 접근
- `Array<Int>`와 `IntArray`의 차이
- `for` 반복문 사용법
- `indices`, `withIndex()` 활용
- `List`, `Set`, `Map` 컬렉션
- 클래스와 생성자
- 주 생성자와 보조 생성자
- 상속과 오버라이딩
- `data class`
- `companion object`

---

# 1. 배열

Kotlin에서 배열은 여러 개의 값을 하나의 변수로 관리할 때 사용한다.  
배열은 생성할 때 크기가 정해지고, 각 데이터는 인덱스를 통해 접근한다.

```kotlin
val data1 = Array(3) { 0 }
val data2 = Array(5) { i -> i * 2 }
