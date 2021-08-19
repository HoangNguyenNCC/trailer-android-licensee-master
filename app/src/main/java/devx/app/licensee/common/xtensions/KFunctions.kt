package company.coutloot.common.xtensions

fun avoidException(body: () -> Unit) {
    try {
        body
    } catch (e: Exception) {
        println("Kotlin Exception"+e.localizedMessage)
    }
}

public fun main(args: Array<String>) {
    main()
}
fun main() {

    avoidException { var s:String?=null

        if(s.equals("")){
            println("non NPE")
        }

        println("Code after NPE")

    }

    println("Code after avoidException")

}