package com.example.criptowallet
import android.content.Context
import androidx.room.*

@Entity(tableName = "coin_holdings")
data class CoinHolding(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val symbol: String,
    val quantity: Double,
    val pricePaid: Double
)

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coin: CoinHolding)

    @Query("SELECT * FROM coin_holdings")
    suspend fun getAll(): List<CoinHolding>

    @Query("SELECT * FROM coin_holdings WHERE name = :name AND pricePaid = :pricePaid LIMIT 1")
    suspend fun findCoin(name: String, pricePaid: Double): CoinHolding?

    @Delete
    suspend fun delete(coin: CoinHolding)
}

@Database(entities = [CoinHolding::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crypto_wallet_db"
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}