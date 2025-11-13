package com.example.mobdev

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobdev.model.SalesItem
import com.example.mobdev.screens.SalesItemAdd
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class SalesItemAddTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyFields_showValidationErrors() {
        composeTestRule.setContent {
            SalesItemAdd(
                currentUserEmail = "test@example.com",
                addSalesItem = {},
                navigateBack = {}
            )
        }

        composeTestRule.onNode(hasText("Add Sales Item") and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Description cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter a valid number").assertIsDisplayed()
    }

    @Test
    fun invalidImageUrl_showsError() {
        composeTestRule.setContent {
            SalesItemAdd(
                currentUserEmail = "test@example.com",
                addSalesItem = {},
                navigateBack = {}
            )
        }


        composeTestRule.onNodeWithText("Description").performTextInput("Test item")
        composeTestRule.onNodeWithText("Price").performTextInput("100")


        composeTestRule.onNodeWithText("Image URL").performTextInput("invalid-url")


        composeTestRule.onNode(hasText("Add Sales Item") and hasClickAction()).performClick()


        composeTestRule.onNodeWithText("Enter a valid URL").assertIsDisplayed()
    }

    @Test
    fun validInput_callsAddSalesItem() {
        var addedItem: SalesItem? = null

        composeTestRule.setContent {
            SalesItemAdd(
                currentUserEmail = "test@example.com",
                addSalesItem = { item -> addedItem = item },
                navigateBack = {}
            )
        }


        composeTestRule.onNodeWithText("Description").performTextInput("Test item")
        composeTestRule.onNodeWithText("Price").performTextInput("100")
        composeTestRule.onNodeWithText("Image URL").performTextInput("https://example.com/img.png")


        composeTestRule.onNode(hasText("Add Sales Item") and hasClickAction()).performClick()


        assertNotNull("addSalesItem should be called", addedItem)
        assertEquals("Test item", addedItem?.description)
        assertEquals(100, addedItem?.price)
        assertEquals("https://example.com/img.png", addedItem?.pictureUrl)
        assertEquals("test@example.com", addedItem?.sellerEmail)
    }

    @Test
    fun nullCurrentUser_displaysLoginMessage() {
        composeTestRule.setContent {
            SalesItemAdd(
                currentUserEmail = null,
                addSalesItem = {},
                navigateBack = {}
            )
        }

        composeTestRule.onNodeWithText("You must be logged in to add a sales item.")
            .assertIsDisplayed()
    }
}