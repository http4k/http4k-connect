package org.http4k.connect.amazon.dynamodb.mapper

import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.DynamoDbAction
import org.http4k.connect.amazon.dynamodb.action.Query
import org.http4k.connect.amazon.dynamodb.action.Scan
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues
import org.http4k.connect.amazon.dynamodb.model.with
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.UUID

private val hashKey = Attribute.uuid().required("hash")
private val sortKey = Attribute.string().required("sort")
private val intAttr = Attribute.int().optional("aNumber")
private val anotherIntAttr = Attribute.int().optional("anotherNumber")
private val stringAttr = Attribute.string().optional("aString")

class DynamoDbQueryBuilderTest {

    class MockDynamoDb : DynamoDb {
        var action: Action<*>? = null

        override fun <R : Any> invoke(action: DynamoDbAction<R>): Result<R, RemoteFailure> {
            this.action = action
            return action.toResult(Response(Status.OK))
        }
    }

    data object MockDocument

    private val mockDynamoDb = MockDynamoDb()
    private val table =
        mockDynamoDb.tableMapper<MockDocument, UUID, String>(TableName.of("Table"), hashKey, sortKey)
    private val index = table.primaryIndex()

    private val uuid = UUID(0, 0)

    @Nested
    inner class ScanTests {

        @Test
        fun `scan with equals filter`() {
            // when
            index.scan(PageSize = 20, ConsistentRead = true) {
                filterExpression {
                    sortKey eq "bar"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a = :a"),
                        scanHasAttributeNames(mapOf("#a" to sortKey.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKey.asValue("bar"))),
                        scanHasLimit(20),
                        scanHasConsistentRead(true)
                    )
                )
            )
        }

        @Test
        fun `scan with not equals filter`() {
            // when
            index.scan {
                filterExpression {
                    hashKey ne uuid
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a <> :a"),
                        scanHasAttributeNames(mapOf("#a" to hashKey.name)),
                        scanHasAttributeValues(mapOf(":a" to hashKey.asValue(uuid))),
                        scanHasLimit(null),
                        scanHasConsistentRead(null)
                    )
                )
            )
        }

        @Test
        fun `scan with greater than filter`() {
            // when
            index.scan {
                filterExpression {
                    sortKey gt "baz"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a > :a"),
                        scanHasAttributeNames(mapOf("#a" to sortKey.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKey.asValue("baz")))
                    )
                )
            )
        }

        @Test
        fun `scan with greater or equal filter`() {
            // when
            index.scan {
                filterExpression {
                    sortKey ge "baz"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a >= :a"),
                        scanHasAttributeNames(mapOf("#a" to sortKey.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKey.asValue("baz")))
                    )
                )
            )
        }

        @Test
        fun `scan with less than filter`() {
            // when
            index.scan {
                filterExpression {
                    intAttr lt 5
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a < :a"),
                        scanHasAttributeNames(mapOf("#a" to intAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to intAttr.asValue(5)))
                    )
                )
            )
        }

        @Test
        fun `scan with less or equal filter`() {
            // when
            index.scan {
                filterExpression {
                    intAttr le 17
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a <= :a"),
                        scanHasAttributeNames(mapOf("#a" to intAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to intAttr.asValue(17)))
                    )
                )
            )
        }

        @Test
        fun `scan with equals attribute filter`() {
            // when
            index.scan {
                filterExpression {
                    intAttr eq anotherIntAttr
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a = #b"),
                        scanHasAttributeNames(mapOf("#a" to intAttr.name, "#b" to anotherIntAttr.name)),
                        scanHasAttributeValues(emptyMap())
                    )
                )
            )
        }

        @Test
        fun `scan with BETWEEN filter`() {
            // when
            index.scan {
                filterExpression {
                    between(intAttr, 17, 23)
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a BETWEEN :a1 AND :a2"),
                        scanHasAttributeNames(mapOf("#a" to intAttr.name)),
                        scanHasAttributeValues(
                            mapOf(
                                ":a1" to intAttr.asValue(17),
                                ":a2" to intAttr.asValue(23)
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `scan with IN filter`() {
            // when
            index.scan {
                filterExpression {
                    intAttr `in` listOf(3, 5, 8, 13)
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a IN (:a0,:a1,:a2,:a3)"),
                        scanHasAttributeNames(mapOf("#a" to intAttr.name)),
                        scanHasAttributeValues(
                            mapOf(
                                ":a0" to intAttr.asValue(3),
                                ":a1" to intAttr.asValue(5),
                                ":a2" to intAttr.asValue(8),
                                ":a3" to intAttr.asValue(13),
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `scan with attribute_exists filter`() {
            // when
            index.scan {
                filterExpression {
                    attributeExists(intAttr)
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("attribute_exists(#a)"),
                        scanHasAttributeNames(mapOf("#a" to intAttr.name)),
                        scanHasAttributeValues(emptyMap()),
                    )
                )
            )
        }

        @Test
        fun `scan with attribute_not_exists filter`() {
            // when
            index.scan {
                filterExpression {
                    attributeNotExists(intAttr)
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("attribute_not_exists(#a)"),
                        scanHasAttributeNames(mapOf("#a" to intAttr.name)),
                        scanHasAttributeValues(emptyMap()),
                    )
                )
            )
        }

        @Test
        fun `scan with begins_with filter`() {
            // when
            index.scan {
                filterExpression {
                    sortKey beginsWith "A"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("begins_with(#a,:a)"),
                        scanHasAttributeNames(mapOf("#a" to sortKey.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKey.asValue("A"))),
                    )
                )
            )
        }

        @Test
        fun `scan with contains filter`() {
            // when
            index.scan {
                filterExpression {
                    sortKey contains "X"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("contains(#a,:a)"),
                        scanHasAttributeNames(mapOf("#a" to sortKey.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKey.asValue("X")))
                    )
                )
            )
        }

        @Test
        fun `scan with logical operators in filter`() {
            // when
            index.scan {
                filterExpression {
                    ((hashKey ne uuid) and not(sortKey beginsWith "A")) or (attributeExists(intAttr) and between(
                        intAttr,
                        100,
                        200
                    ))
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("((#a <> :a AND (NOT begins_with(#b,:b))) OR (attribute_exists(#c) AND #d BETWEEN :d1 AND :d2))"),
                        scanHasAttributeNames(
                            mapOf(
                                "#a" to hashKey.name,
                                "#b" to sortKey.name,
                                "#c" to intAttr.name,
                                "#d" to intAttr.name
                            )
                        ),
                        scanHasAttributeValues(
                            mapOf(
                                ":a" to hashKey.asValue(uuid),
                                ":b" to sortKey.asValue("A"),
                                ":d1" to intAttr.asValue(100),
                                ":d2" to intAttr.asValue(200)
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `scanPage with complex filter`() {
            // when
            index.scanPage(ExclusiveStartKey = Pair(uuid, "B"), Limit = 20, ConsistentRead = true) {
                filterExpression {
                    hashKey eq uuid and (sortKey beginsWith "foo" or attributeExists(intAttr))
                }
            }

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("(#a = :a AND (begins_with(#b,:b) OR attribute_exists(#c)))"),
                        scanHasAttributeNames(mapOf("#a" to hashKey.name, "#b" to sortKey.name, "#c" to intAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to hashKey.asValue(uuid), ":b" to sortKey.asValue("foo"))),
                        scanHasExclusiveStartKey(Item().with(hashKey of uuid, sortKey of "B")),
                        scanHasLimit(20),
                        scanHasConsistentRead(true)
                    )
                )
            )
        }
    }

    @Nested
    inner class QueryTests {

        @Test
        fun `query with hash key condition`() {
            // when
            index.query(ScanIndexForward = false, PageSize = 10, ConsistentRead = true) {
                keyCondition {
                    hashKey eq uuid
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a"),
                        queryHasFilterExpression(null),
                        queryHasAttributeNames(mapOf("#a" to hashKey.name)),
                        queryHasAttributeValues(mapOf(":a" to hashKey.asValue(uuid))),
                        queryHasScanIndexForward(false),
                        queryHasLimit(10),
                        queryHasConsistentRead(true)
                    )
                )
            )
        }

        @Test
        fun `query with hash key and missing sort key condition`() {
            // when
            index.query {
                keyCondition {
                    (hashKey eq uuid) and null
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a"),
                        queryHasFilterExpression(null),
                        queryHasAttributeNames(mapOf("#a" to hashKey.name)),
                        queryHasAttributeValues(mapOf(":a" to hashKey.asValue(uuid))),
                        queryHasScanIndexForward(true), // default
                        queryHasLimit(null),
                        queryHasConsistentRead(null)
                    )
                )
            )
        }

        @Test
        fun `query with hash key and sort key operator condition`() {
            // when
            index.query {
                keyCondition {
                    (hashKey eq uuid) and (sortKey lt "B")
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a AND #b < :b"),
                        queryHasFilterExpression(null),
                        queryHasAttributeNames(mapOf("#a" to hashKey.name, "#b" to sortKey.name)),
                        queryHasAttributeValues(mapOf(":a" to hashKey.asValue(uuid), ":b" to sortKey.asValue("B")))
                    )
                )
            )
        }

        @Test
        fun `query with hash key and sort key between condition`() {
            // when
            index.query {
                keyCondition {
                    (hashKey eq uuid) and between(sortKey, "a", "h")
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a AND #b BETWEEN :b1 AND :b2"),
                        queryHasFilterExpression(null),
                        queryHasAttributeNames(mapOf("#a" to hashKey.name, "#b" to sortKey.name)),
                        queryHasAttributeValues(
                            mapOf(
                                ":a" to hashKey.asValue(uuid),
                                ":b1" to sortKey.asValue("a"),
                                ":b2" to sortKey.asValue("h")
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `query with hash key and sort key begins_with condition`() {
            // when
            index.query {
                keyCondition {
                    (hashKey eq uuid) and (sortKey beginsWith "S")
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a AND begins_with(#b,:b)"),
                        queryHasFilterExpression(null),
                        queryHasAttributeNames(mapOf("#a" to hashKey.name, "#b" to sortKey.name)),
                        queryHasAttributeValues(mapOf(":a" to hashKey.asValue(uuid), ":b" to sortKey.asValue("S")))
                    )
                )
            )
        }

        @Test
        fun `query with key condition and filter expression`() {
            // when
            index.query {
                keyCondition {
                    (hashKey eq uuid) and (sortKey gt "A")
                }
                filterExpression {
                    attributeNotExists(intAttr) or (intAttr eq 0)
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a AND #b > :b"),
                        queryHasFilterExpression("(attribute_not_exists(#c) OR #d = :d)"),
                        queryHasAttributeNames(
                            mapOf(
                                "#a" to hashKey.name,
                                "#b" to sortKey.name,
                                "#c" to intAttr.name,
                                "#d" to intAttr.name,
                            )
                        ),
                        queryHasAttributeValues(
                            mapOf(
                                ":a" to hashKey.asValue(uuid),
                                ":b" to sortKey.asValue("A"),
                                ":d" to intAttr.asValue(0)
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `queryPage with key condition and filter expression`() {
            // when
            index.queryPage(ScanIndexForward = false, Limit = 50, ConsistentRead = true, ExclusiveStartKey = "start") {
                keyCondition {
                    (hashKey eq uuid) and (sortKey ge "A")
                }
                filterExpression {
                    attributeNotExists(intAttr) or (intAttr eq 0) or (intAttr ne anotherIntAttr)
                }
            }

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a AND #b >= :b"),
                        queryHasFilterExpression("((attribute_not_exists(#c) OR #d = :d) OR #e <> #f)"),
                        queryHasAttributeNames(
                            mapOf(
                                "#a" to hashKey.name,
                                "#b" to sortKey.name,
                                "#c" to intAttr.name,
                                "#d" to intAttr.name,
                                "#e" to intAttr.name,
                                "#f" to anotherIntAttr.name,
                            )
                        ),
                        queryHasAttributeValues(
                            mapOf(
                                ":a" to hashKey.asValue(uuid),
                                ":b" to sortKey.asValue("A"),
                                ":d" to intAttr.asValue(0)
                            )
                        ),
                        queryHasScanIndexForward(false),
                        queryHasLimit(50),
                        queryHasConsistentRead(true),
                        queryHasExclusiveStartKey(Item().with(hashKey of uuid, sortKey of "start"))
                    )
                )
            )
        }
    }

    companion object {
        @JvmStatic
        fun optionalLogicalOperandsSource(): Iterable<Arguments> = listOf(
            Arguments.of(null, null, null, null, null),
            Arguments.of(
                42,
                null,
                "#a = :a",
                mapOf("#a" to intAttr.name),
                mapOf(":a" to intAttr.asValue(42))
            ),
            Arguments.of(
                null,
                "foo",
                "#a = :a",
                mapOf("#a" to stringAttr.name),
                mapOf(":a" to stringAttr.asValue("foo"))
            ),
            Arguments.of(
                42,
                "foo",
                "(#a = :a AND #b = :b)",
                mapOf("#a" to intAttr.name, "#b" to stringAttr.name),
                mapOf(":a" to intAttr.asValue(42), ":b" to stringAttr.asValue("foo"))
            )
        )
    }

    @ParameterizedTest
    @MethodSource("optionalLogicalOperandsSource")
    fun `filterExpression with optional logical operands`(
        intValue: Int?,
        stringValue: String?,
        expectedFilterExpression: String?,
        expectedAttributeNames: TokensToNames?,
        expectedAttributeValues: TokensToValues?
    ) {
        // when
        index.scan {
            filterExpression {
                val intFilter = intValue?.let { intAttr eq it }
                val stringFilter = stringValue?.let { stringAttr eq it }

                intFilter and stringFilter
            }
        }.toList()

        // then
        assertThat(
            mockDynamoDb.action as? Scan, present(
                allOf(
                    scanHasFilterExpression(expectedFilterExpression),
                    scanHasAttributeNames(expectedAttributeNames),
                    scanHasAttributeValues(expectedAttributeValues)
                )
            )
        )
    }
}
