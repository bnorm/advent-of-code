fun readResourceText(resource: String): String =
        ClassLoader.getSystemResource(resource).readText()
