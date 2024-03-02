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
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.Select
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues
import org.http4k.connect.amazon.dynamodb.model.asRequired
import org.http4k.connect.amazon.dynamodb.model.with
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.UUID

private val hashKeyAttr = Attribute.uuid().required("hash")
private val sortKeyAttr = Attribute.string().required("sort")
private val intAttr = Attribute.int().optional("aNumber")
private val anotherIntAttr = Attribute.int().optional("anotherNumber")
private val stringAttr = Attribute.string().optional("aString")

class DynamoDbQueryDslTest {

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
        mockDynamoDb.tableMapper<MockDocument, UUID, String>(TableName.of("Table"), hashKeyAttr, sortKeyAttr)
    private val index = table.primaryIndex()
    private val secondaryIndex = DynamoDbTableMapperSchema.GlobalSecondary<Int, Unit>(
        indexName = IndexName.of("Secondary"),
        hashKeyAttribute = intAttr.asRequired(),
        sortKeyAttribute = null
    )

    private val uuid = UUID(0, 0)

    @Nested
    inner class ScanTests {

        @Test
        fun `scan with equals filter`() {
            // when
            index.scan(PageSize = 20, ConsistentRead = true) {
                filterExpression {
                    sortKeyAttr eq "bar"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a = :a"),
                        scanHasAttributeNames(mapOf("#a" to sortKeyAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKeyAttr.asValue("bar"))),
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
                    hashKeyAttr ne uuid
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a <> :a"),
                        scanHasAttributeNames(mapOf("#a" to hashKeyAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to hashKeyAttr.asValue(uuid))),
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
                    sortKeyAttr gt "baz"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a > :a"),
                        scanHasAttributeNames(mapOf("#a" to sortKeyAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKeyAttr.asValue("baz")))
                    )
                )
            )
        }

        @Test
        fun `scan with greater or equal filter`() {
            // when
            index.scan {
                filterExpression {
                    sortKeyAttr ge "baz"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("#a >= :a"),
                        scanHasAttributeNames(mapOf("#a" to sortKeyAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKeyAttr.asValue("baz")))
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
                    intAttr.between(17, 23)
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
                    intAttr isIn listOf(3, 5, 8, 13)
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
                    sortKeyAttr beginsWith "A"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("begins_with(#a,:a)"),
                        scanHasAttributeNames(mapOf("#a" to sortKeyAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKeyAttr.asValue("A"))),
                    )
                )
            )
        }

        @Test
        fun `scan with contains filter`() {
            // when
            index.scan {
                filterExpression {
                    sortKeyAttr contains "X"
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("contains(#a,:a)"),
                        scanHasAttributeNames(mapOf("#a" to sortKeyAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to sortKeyAttr.asValue("X")))
                    )
                )
            )
        }

        @Test
        fun `scan with logical operators in filter`() {
            // when
            index.scan {
                filterExpression {
                    ((hashKeyAttr ne uuid) and not(sortKeyAttr beginsWith "A")) or
                        (attributeExists(intAttr) and intAttr.between(100, 200))
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("((#a <> :a AND (NOT begins_with(#b,:b))) OR (attribute_exists(#c) AND #d BETWEEN :d1 AND :d2))"),
                        scanHasAttributeNames(
                            mapOf(
                                "#a" to hashKeyAttr.name,
                                "#b" to sortKeyAttr.name,
                                "#c" to intAttr.name,
                                "#d" to intAttr.name
                            )
                        ),
                        scanHasAttributeValues(
                            mapOf(
                                ":a" to hashKeyAttr.asValue(uuid),
                                ":b" to sortKeyAttr.asValue("A"),
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
            index.scanPage(
                ExclusiveStartKey = Key(hashKeyAttr of uuid, sortKeyAttr of "B"),
                Limit = 20,
                ConsistentRead = true
            ) {
                filterExpression {
                    hashKeyAttr eq uuid and (sortKeyAttr beginsWith "foo" or attributeExists(intAttr))
                }
            }

            // then
            assertThat(
                mockDynamoDb.action as? Scan, present(
                    allOf(
                        scanHasFilterExpression("(#a = :a AND (begins_with(#b,:b) OR attribute_exists(#c)))"),
                        scanHasAttributeNames(mapOf("#a" to hashKeyAttr.name, "#b" to sortKeyAttr.name, "#c" to intAttr.name)),
                        scanHasAttributeValues(mapOf(":a" to hashKeyAttr.asValue(uuid), ":b" to sortKeyAttr.asValue("foo"))),
                        scanHasExclusiveStartKey(Item().with(hashKeyAttr of uuid, sortKeyAttr of "B")),
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
                        queryHasAttributeNames(mapOf("#a" to hashKeyAttr.name)),
                        queryHasAttributeValues(mapOf(":a" to hashKeyAttr.asValue(uuid))),
                        queryHasScanIndexForward(false),
                        queryHasLimit(10),
                        queryHasConsistentRead(true)
                    )
                )
            )
        }

        @Test
        fun `query on index with hash key condition`() {
            // when
            table.index(secondaryIndex).query {
                keyCondition {
                    hashKey eq 7
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a"),
                        queryHasFilterExpression(null),
                        queryHasAttributeNames(mapOf("#a" to intAttr.name)),
                        queryHasAttributeValues(mapOf(":a" to intAttr.asValue(7)))
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
                        queryHasAttributeNames(mapOf("#a" to hashKeyAttr.name)),
                        queryHasAttributeValues(mapOf(":a" to hashKeyAttr.asValue(uuid))),
                        queryHasScanIndexForward(true), // default
                        queryHasLimit(null),
                        queryHasConsistentRead(null)
                    )
                )
            )
        }

        @Test
        fun `query in index with hash key and ignored sort key condition (because of missing sort key)`() {
            // when
            table.index(secondaryIndex).query {
                keyCondition {
                    (hashKey eq 7) and (sortKey gt Unit)
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a"),
                        queryHasFilterExpression(null),
                        queryHasAttributeNames(mapOf("#a" to intAttr.name)),
                        queryHasAttributeValues(mapOf(":a" to intAttr.asValue(7)))
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
                        queryHasAttributeNames(mapOf("#a" to hashKeyAttr.name, "#b" to sortKeyAttr.name)),
                        queryHasAttributeValues(mapOf(":a" to hashKeyAttr.asValue(uuid), ":b" to sortKeyAttr.asValue("B")))
                    )
                )
            )
        }

        @Test
        fun `query with hash key and sort key between condition`() {
            // when
            index.query {
                keyCondition {
                    (hashKey eq uuid) and sortKey.between("a", "h")
                }
            }.toList()

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a AND #b BETWEEN :b1 AND :b2"),
                        queryHasFilterExpression(null),
                        queryHasAttributeNames(mapOf("#a" to hashKeyAttr.name, "#b" to sortKeyAttr.name)),
                        queryHasAttributeValues(
                            mapOf(
                                ":a" to hashKeyAttr.asValue(uuid),
                                ":b1" to sortKeyAttr.asValue("a"),
                                ":b2" to sortKeyAttr.asValue("h")
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
                        queryHasAttributeNames(mapOf("#a" to hashKeyAttr.name, "#b" to sortKeyAttr.name)),
                        queryHasAttributeValues(mapOf(":a" to hashKeyAttr.asValue(uuid), ":b" to sortKeyAttr.asValue("S")))
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
                                "#a" to hashKeyAttr.name,
                                "#b" to sortKeyAttr.name,
                                "#c" to intAttr.name,
                                "#d" to intAttr.name,
                            )
                        ),
                        queryHasAttributeValues(
                            mapOf(
                                ":a" to hashKeyAttr.asValue(uuid),
                                ":b" to sortKeyAttr.asValue("A"),
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
            index.queryPage(
                ScanIndexForward = false,
                Limit = 50,
                ConsistentRead = true,
                ExclusiveStartKey = Key(hashKeyAttr of uuid, sortKeyAttr of "start")
            ) {
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
                                "#a" to hashKeyAttr.name,
                                "#b" to sortKeyAttr.name,
                                "#c" to intAttr.name,
                                "#d" to intAttr.name,
                                "#e" to intAttr.name,
                                "#f" to anotherIntAttr.name,
                            )
                        ),
                        queryHasAttributeValues(
                            mapOf(
                                ":a" to hashKeyAttr.asValue(uuid),
                                ":b" to sortKeyAttr.asValue("A"),
                                ":d" to intAttr.asValue(0)
                            )
                        ),
                        queryHasScanIndexForward(false),
                        queryHasLimit(50),
                        queryHasConsistentRead(true),
                        queryHasExclusiveStartKey(Item().with(hashKeyAttr of uuid, sortKeyAttr of "start"))
                    )
                )
            )
        }

        @Test
        fun `count with key condition and filter expression`() {
            // when
            index.count {
                keyCondition {
                    (hashKey eq uuid) and (sortKey eq "A")
                }
                filterExpression {
                    intAttr gt anotherIntAttr
                }
            }

            // then
            assertThat(
                mockDynamoDb.action as? Query, present(
                    allOf(
                        queryHasKeyConditionExpression("#a = :a AND #b = :b"),
                        queryHasFilterExpression("#c > #d"),
                        queryHasAttributeNames(
                            mapOf(
                                "#a" to hashKeyAttr.name,
                                "#b" to sortKeyAttr.name,
                                "#c" to intAttr.name,
                                "#d" to anotherIntAttr.name
                            )
                        ),
                        queryHasAttributeValues(
                            mapOf(
                                ":a" to hashKeyAttr.asValue(uuid),
                                ":b" to sortKeyAttr.asValue("A")
                            )
                        ),
                        queryHasSelect(Select.COUNT)
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
