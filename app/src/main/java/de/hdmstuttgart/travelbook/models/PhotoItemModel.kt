package de.hdmstuttgart.travelbook.models

import androidx.room.*

@Entity(tableName = "photoitemmodel",
    foreignKeys = [ForeignKey(
        entity = TravelbookModel::class,
        childColumns = ["travelbook_fk"],
                parentColumns = ["id"],
    onDelete = ForeignKey.CASCADE
    )])

data class PhotoItemModel(
// vllt noch datum
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name ="photo") var photo: String?,
    @ColumnInfo(name = "comment") var comment: String?,
    @ColumnInfo(name = "travelbook_fk") var travelbookId: Int
){}