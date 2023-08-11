package org.yaproxy.yap

import java.io.File

data class GitHubRepo(val owner: String, val name: String, val dir: File? = null) {

    override fun toString() = "$owner/$name"
}
