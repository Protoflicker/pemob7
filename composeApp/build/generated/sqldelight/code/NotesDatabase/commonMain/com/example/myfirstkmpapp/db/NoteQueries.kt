package com.example.myfirstkmpapp.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class NoteQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: Long,
    title: String,
    content: String,
    created_at: Long,
  ) -> T): Query<T> = Query(-2_121_522_479, arrayOf("Note"), driver, "Note.sq", "selectAll",
      "SELECT * FROM Note ORDER BY created_at DESC") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!
    )
  }

  public fun selectAll(): Query<Note> = selectAll { id, title, content, created_at ->
    Note(
      id,
      title,
      content,
      created_at
    )
  }

  public fun <T : Any> selectById(id: Long, mapper: (
    id: Long,
    title: String,
    content: String,
    created_at: Long,
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!
    )
  }

  public fun selectById(id: Long): Query<Note> = selectById(id) { id_, title, content, created_at ->
    Note(
      id_,
      title,
      content,
      created_at
    )
  }

  public fun <T : Any> search(
    title: String,
    content: String,
    mapper: (
      id: Long,
      title: String,
      content: String,
      created_at: Long,
    ) -> T,
  ): Query<T> = SearchQuery(title, content) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!
    )
  }

  public fun search(title: String, content: String): Query<Note> = search(title, content) { id,
      title_, content_, created_at ->
    Note(
      id,
      title_,
      content_,
      created_at
    )
  }

  public fun insert(
    title: String,
    content: String,
    created_at: Long,
  ) {
    driver.execute(-854_954_387, """
        |INSERT INTO Note(title, content, created_at)
        |VALUES (?, ?, ?)
        """.trimMargin(), 3) {
          bindString(0, title)
          bindString(1, content)
          bindLong(2, created_at)
        }
    notifyQueries(-854_954_387) { emit ->
      emit("Note")
    }
  }

  public fun update(
    title: String,
    content: String,
    id: Long,
  ) {
    driver.execute(-510_008_195, """UPDATE Note SET title = ?, content = ? WHERE id = ?""", 3) {
          bindString(0, title)
          bindString(1, content)
          bindLong(2, id)
        }
    notifyQueries(-510_008_195) { emit ->
      emit("Note")
    }
  }

  public fun delete(id: Long) {
    driver.execute(-1_006_620_321, """DELETE FROM Note WHERE id = ?""", 1) {
          bindLong(0, id)
        }
    notifyQueries(-1_006_620_321) { emit ->
      emit("Note")
    }
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Note", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Note", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_342_646_110, """SELECT * FROM Note WHERE id = ?""", mapper, 1) {
      bindLong(0, id)
    }

    override fun toString(): String = "Note.sq:selectById"
  }

  private inner class SearchQuery<out T : Any>(
    public val title: String,
    public val content: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Note", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Note", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-577_498_788,
        """SELECT * FROM Note WHERE title LIKE ? OR content LIKE ?""", mapper, 2) {
      bindString(0, title)
      bindString(1, content)
    }

    override fun toString(): String = "Note.sq:search"
  }
}
