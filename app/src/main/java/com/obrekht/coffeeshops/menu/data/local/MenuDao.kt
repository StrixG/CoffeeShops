package com.obrekht.coffeeshops.menu.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.obrekht.coffeeshops.menu.data.model.MenuItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {

    @Query("SELECT * FROM menu_item WHERE coffeeShopId == :coffeeShopId ORDER BY id ASC")
    fun observeByCoffeeShopId(coffeeShopId: Long): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_item WHERE id IN (:idList)")
    fun observeByIds(idList: List<Long>): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_item WHERE id = :id")
    suspend fun getById(id: Long): MenuItemEntity?

    @Upsert
    suspend fun upsert(menuItem: MenuItemEntity)

    @Upsert
    suspend fun upsert(menuItemList: List<MenuItemEntity>)

    @Query("DELETE FROM menu_item WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM menu_item")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(menuItemList: List<MenuItemEntity>) {
        deleteAll()
        upsert(menuItemList)
    }
}
