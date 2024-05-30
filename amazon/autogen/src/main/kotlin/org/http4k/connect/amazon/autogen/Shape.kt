package org.http4k.connect.amazon.autogen

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(Shape.StringShape::class, name = "string"),
    JsonSubTypes.Type(Shape.LongShape::class, name = "long"),
    JsonSubTypes.Type(Shape.FloatShape::class, name = "float"),
    JsonSubTypes.Type(Shape.IntShape::class, name = "integer"),
    JsonSubTypes.Type(Shape.DoubleShape::class, name = "double"),
    JsonSubTypes.Type(Shape.BooleanShape::class, name = "boolean"),
    JsonSubTypes.Type(Shape.BlobShape::class, name = "blob"),
    JsonSubTypes.Type(Shape.TimestampShape::class, name = "timestamp"),
    JsonSubTypes.Type(Shape.Structure::class, name = "structure"),
    JsonSubTypes.Type(Shape.MapShape::class, name = "map"),
    JsonSubTypes.Type(Shape.ListShape::class, name = "list"),
)
sealed interface Shape {
    data class StringShape(
        val pattern: Regex? = null,
        val enum: List<String>? = emptyList()
    ) : Shape

    data object BooleanShape : Shape
    data object BlobShape : Shape
    data object DoubleShape : Shape
    data object TimestampShape : Shape
    data object FloatShape : Shape

    data class LongShape(
        val box: Boolean?
    ) : Shape {
        val isNullable = box != null
    }

    data class IntShape(
        private val box: Boolean?,
        private val min: Int?,
        private val max: Int?,
    ) : Shape {
        val isNullable = box != null
    }

    data class Structure(
        val members: Map<MemberName, ShapeRef>,
        val documentation: Documentation?,
        val required: List<MemberName>?
    ) : Shape

    data class MapShape(
        val key: ShapeRef,
        val `value`: ShapeRef,
        val flattened: Boolean? = false
    ) : Shape

    data class ListShape(
        val member: ShapeRef,
        val flattened: Boolean? = false
    ) : Shape
}
