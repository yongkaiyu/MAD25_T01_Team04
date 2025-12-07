package np.ict.mad.t01_team04.ui.theme

//for testing
object FakeAuth {
    private val validUsers = mapOf(
        "admin" to "123456",
        "test" to "password",
        "user" to "user123"
    )

    fun login(username: String, password: String): Boolean {
        return validUsers[username] == password
    }

    fun createUser(username: String, password: String): Boolean {
        if (validUsers.containsKey(username)) return false
        // Fake: we can't permanently save, but we simulate success
        return true
    }
}
