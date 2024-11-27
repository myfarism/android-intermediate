package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem


object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0 until 100) {  // Pastikan loopnya tepat
            val story = ListStoryItem(
                id = i.toString(),
                name = "name $i",
                description = "description $i",
                photoUrl = "http://dummyurl.com/$i.jpg"
            )
            items.add(story)
        }
        return items
    }
}
