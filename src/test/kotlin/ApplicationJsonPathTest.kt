package com.example

//import io.ktor.client.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import io.ktor.server.routing.*
//import io.ktor.server.testing.*

class ApplicationJsonPathTest {
//    @Test
//    fun tasksCanBeFound() = testApplication {
//        application {
//            val repository = FakeTaskRepository()
//            val service = TaskServiceImpl(repository)
//            routing {
//                configureRestRoutes(service)
//            }
//        }
//
//        val client = createClient {
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//
//        val jsonDoc = client.getAsJsonPath("/tasks")
//
//        val result: List<String> = jsonDoc.read("$[*].name")
//        assertEquals("cleaning", result[0])
//        assertEquals("gardening", result[1])
//        assertEquals("shopping", result[2])
//    }
//
//    @Test
//    fun tasksCanBeFoundByPriority() = testApplication {
//        application {
//            val repository = FakeTaskRepository()
//            val service = TaskServiceImpl(repository)
//            routing {
//                configureRestRoutes(service)
//            }
//        }
//
//        val client = createClient {
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//        val priority = Priority.MEDIUM
//        val jsonDoc = client.getAsJsonPath("/tasks/byPriority/$priority")
//
//        val result: List<String> =
//            jsonDoc.read("$[?(@.priority == '$priority')].name")
//        assertEquals(2, result.size)
//
//        assertEquals("gardening", result[0])
//        assertEquals("painting", result[1])
//    }
//
//    suspend fun HttpClient.getAsJsonPath(url: String): DocumentContext {
//        val response = this.get(url) {
//            accept(ContentType.Application.Json)
//        }
//        return JsonPath.parse(response.bodyAsText())
//    }
}
