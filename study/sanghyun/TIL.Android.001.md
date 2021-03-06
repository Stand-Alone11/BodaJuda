**TIL > Android**

<br>

## ๐ฅ TIL: Android

> * ์์ฑ์: Sanghyun Park
> * ์ต๊ทผ ์์ ์ผ์: 2022. **03. 04.** (๊ธ)



<br>

#### 001. Android App ๋ง๋ค๊ธฐ์ ์คํํ๊ธฐ

<br>

**View Binding**

> *View binding* is a feature that allows you to more easily write code that interacts with views. Once view binding is enabled in a module, it generates a *binding class* for each XML layout file present in that module. An instance of a binding class contains direct references to all views that have an ID in the corresponding layout.

<br>

```kotlin
// build.gradle (Module)

android {
	buildFeatures {
        viewBinding true
    }
}
```

<br>

```kotlin
val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    binding.btnSay.setOnClickListener {
        binding.textSay.text = "Hello, Kotlin! and Android!"
        
        for (i in 1..10) {
	        binding.textSay.append("Hello! ${i}\n")            
        }
    }
}
```



<br>

#### 002. Android์์ Log๋ฅผ ์ฌ์ฉํ๋ ๋ฐฉ๋ฒ

<br>

```kotlin
val TAG = "MainActivity"

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    binding.btnSay.setOnClickListener {
        Log.d(TAG, "button touched")
    }
}
```



<br>

#### 003. ํด๋์ค

<br>

```kotlin
class Log {
    // ์ธ์คํด์ค ์์ฑ ์์ด ๋ฐ๋ก ์ ๊ทผ ๊ฐ๋ฅ
    companion object {
        val name = "James"
        fun d(tag: String, msg: String) {
			println("[$tag]: $msg")
        }
    }
}

println(Log.name)
Log.d("FE", "organize the vuex")
```



<br>

#### 004. Null Safety

<br>

```kotlin
var myNum = 7

// nullable values
var myName: Int? = null

// NPE: myName์๋ `null`์ด ๋ด๊ฒจ ์์
var result = myName.plus(13)

// safe calls
// myName์๋ `null`์ด ๋ด๊ฒจ ์๊ธฐ ๋๋ฌธ์ `.plus()`๊ฐ ์คํ๋์ง ์์, ๊ทธ๋๋ก `null`
var result = myName?.plus(13)

// Elvis operator
var result = myName?.plus(13) ?: 0
```



<br>

#### 005. ์ง์ฐ ์ด๊ธฐํ

<br>

```kotlin
class Person {
	val name = "James"
    val age = 27
    val address = "Seoul"
    val contact = "2dend0713@gmail.com"
}

// ์ ์ธ ๋จผ์  ํ๊ณ  ๋์ค์ ์ด๊ธฐํ ํด์ ์ฌ์ฉ
lateinit var person: Person
person = Person()

// ๋์ค์ ํธ์ถํด์ ์ฌ์ฉ
val person by lazy { Person() }
println(person.name)
```



<br>

#### 006. ์ค์ฝํ ํจ์

<br>

```kotlin
data class Person (val name: String, val contact: String, val age: Int)

class SeoulPeople {
    var people = mutableListOf<Person>()
    init {
        people.add(Person("James", "010-6655-3446", "27"))
        people.add(Person("Lucy", "010-6123-4421", "28"))
        people.add(Person("Duke", "010-4456-7687", "29"))
    }
}

seoulPeople = SeoulPeople()

// run: ์ค์ฝํ ๋ด ๋ง์ง๋ง ์คํ๋ฌธ์ ๊ฒฐ๊ณผ ๋ฐํ
val result = seoulPeople.people.run {
    add(Person("Kelly", "010-43657-2276, "24"))
	size
}

// let using alias: ์ค์ฝํ ๋ด ๋ง์ง๋ง ์คํ๋ฌธ์ ๊ฒฐ๊ณผ ๋ฐํ
val result = seoulPeople.people.let { people ->
    people.add(Person("Kelly", "010-43657-2276, "24"))
    size
}
                      
// apply: ์๊ธฐ ์์  ๋ฐํ
val result = seoulPeople.people.apply {
    add(Person("Kelly", "010-43657-2276, "24"))
}

// also using alias: ์๊ธฐ ์์  ๋ฐํ
val result = seoulPeople.people.also { people ->
    people.add(Person("Kelly", "010-43657-2276, "24"))
}

// with
val binding lazy { ActivityMainBinding.inflate(layoutInflater) }
with(binding) {
    button.setOnClickListener { ... }
    imageView.setImageLevel(50)
    textView.text = "Hello"
}
```

