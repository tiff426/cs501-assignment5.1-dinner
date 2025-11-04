package com.example.whatsfordinner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.whatsfordinner.ui.theme.WhatsForDinnerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatsForDinnerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WhatsForDinnerTheme {
        Greeting("Android")
    }
}


// nav stuff?

sealed class Routes(val route : String) {
    object Home : Routes("home")
    object Details : Routes("details")
    object Recipe : Routes("recipe")
}

NavHost(navController = navController, startDestinatino = Routes.Home.route) {
    composable(Routes.Home.route + "/{id}") { // what is the point of  + "/{id}" --> oh wait i think id is the variable to pass across screens?
        Home()
    }

    composable(Routes.Details.route + "/{id}") {
        Details()
    }

    composable(Routes.Recipe.route + "/{id}") {
        Recipe()
    }
}
//• Home screen: list of recipe names (use LazyColumn)
//• Detail screen: displays full recipe (title, ingredients, steps) using data passed via arguments
//• Add Recipe screen: form for entering new recipe (basic state management)
//• Use NavHostController, NavHost, and define a sealed Routes class
//• Use navigate(route + "/{id}") to pass an argument and read it via backStackEntry
//• Use popUpTo() to control stack behavior when adding a new recipe
//• Prevent multiple copies of the same screen using launchSingleTop
//• Style and layout using Scaffold and consistent navigation
//• Implement a BottomNavigation bar for “Home”, “Add”, and “Settings”; Persist recipes using in-memory state in ViewModel

// screens
@Composable
fun MyApp() {
    // create navController instance
    val navController = rememberNavController()
    // log changes if you want
//    LaunchedEffect(navController) {
//        navController.currentBackStack.collect { backStackEntries ->
//            val routeList = backStackEntries.joinToString(separator = " -> ") { it.destination.route ?: "null" }
//            Log.d(TAG, "Current Back Stack: $routeList")
//        }
//    }

    // create navhost -> container for all of my routes
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // where the actual composables are defined with their route
        composable(route = "home") {
            HomeScreen(navController = navController)
        }

        composable(route = "details") {
            DetailsScreen(navController = navController)
        }

        composable(route = "recipe") {
            RecipeScreen(navController = navController)
        }
    }
}

// actually create the screens now
@Composable
fun HomeScreen(navController : NavController) {
    val placeholders = listOf(
        "hello",
        "hello again",
        "goodbye"
    )
    Column(
        modifier = Modifier.padding(top = 24.dp)
    ) {
        Text(text = "Home")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                Text(message)
            }
        }
    }
}