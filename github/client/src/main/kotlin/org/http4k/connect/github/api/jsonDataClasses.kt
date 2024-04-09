package org.http4k.connect.github.api

import org.http4k.core.Uri

data class GetGitRefData(val ref: String,
                         val node_id: String,
                         val uri: Uri,
                         val `object`: Object) {
    data class Object(val type: String,
                      val sha: String,
                      val uri: Uri)
}

data class CreateRefData(val ref: String)

data class UploadContentData(val content: Content,
                             val commit: Commit) {
    data class Content(val name: String,
                       val path: String,
                       val sha: String,
                       val size: Int,
                       val uri: Uri,
                       val html_uri: Uri,
                       val git_uri: Uri,
                       val download_uri: Uri,
                       val type: String,
                       val _links: Link)

    data class Commit(val sha: String,
                      val node_id: String,
                      val uri: Uri,
                      val html_uri: Uri,
                      val author: Person,
                      val committer: Person,
                      val message: String,
                      val tree: Tree,
                      val parents: Array<Parent>,
                      val verification: Verification)

    data class Person(val name: String, val email: String, val date: String? = null) {
        val body by lazy { """{"name":"$name","email":"$email"${date?.let { ""","date":"$it"""" } ?: ""}}""" }
    }

    data class Tree(val uri: Uri, val sha: String)
    data class Parent(val uri: Uri, val html_uri: Uri, val sha: String)
    data class Verification(val verified: Boolean,
                            val reason: String,
                            val signature: String?,
                            val payload: String?)
}

data class PushRequest(val uri: Uri,
                       val id: Int,
                       val node_id: String,
                       val html_uri: Uri,
                       val diff_uri: Uri,
                       val patch_uri: Uri,
                       val issue_uri: Uri,
                       val commits_uri: Uri,
                       val review_comments_uri: Uri,
                       val review_comment_uri: Uri,
                       val comments_uri: Uri,
                       val statuses_uri: Uri,
                       val number: Int,
                       val state: State,
                       val locked: Boolean,
                       val title: String,
                       val user: User,
                       val body: String?,
                       val labels: Array<Label>) {
    enum class State { open, closed }
    data class User(val name: String?,
                    val email: String?,
                    val login: String,
                    val id: Int,
                    val node_id: String,
                    val avatar_uri: Uri,
                    val gravatar_id: String,
                    val uri: Uri,
                    val html_uri: Uri,
                    val followers_uri: Uri,
                    val following_uri: Uri,
                    val gists_uri: Uri,
                    val starred_uri: Uri,
                    val subscriptions_uri: Uri,
                    val organizations_uri: Uri,
                    val repos_uri: Uri,
                    val events_uri: Uri,
                    val received_events_uri: Uri,
                    val type: String,
                    val site_admin: Boolean,
                    val starred_at: String?)

    data class Label(val id: Long,
                     val node_id: String,
                     val uri: Uri,
                     val name: String,
                     val description: String,
                     val color: String,
                     val default: Boolean)
    // TODO finish
}

data class PullRequests(val pullRequests: Array<PushRequest>)

data class CommitData(val uri: Uri,
                      val sha: String,
                      val node_id: String,
                      val html_uri: Uri,
                      val comments_uri: Uri,
                      val commit: Commit) {
    data class Commit(
        val uri: Uri,
        // TODO finish
    )
    // TODO finish
}

data class PullRequestMergeResult(val sha: String,
                                  val merged: Boolean,
                                  val message: String)

enum class MergeMethod { merge, squash, rebase }
