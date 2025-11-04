package com.example.whatsfordinner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.whatsfordinner.ui.theme.WhatsForDinnerTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.getValue
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// custom recipe data object
data class Recipe(val title: String, val ingredients: String, val steps: String)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatsForDinnerTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // We call our main composable which will set up the navigation.
                    MyApp()
                }
            }
        }
    }
}

class MyViewModel : ViewModel() {
    // for persistent recipes?
    val recipes: SnapshotStateList<Recipe> = mutableStateListOf()

    fun addRecipe(recipe: Recipe) {
        recipes.add(recipe)
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    WhatsForDinnerTheme {
//        Greeting("Android")
//    }
//}
//

// nav stuff?

sealed class Routes(val route : String, val title : String, val icon : ImageVector) {
    object Home : Routes("home", "Home", Icons.Default.Home)
    object Details : Routes("details", "Details", Icons.Default.Build) // this shouldnt be in navbar tho
    object Recipe : Routes("recipe", "Recipe", Icons.Default.Add)
    object Settings : Routes("settings", "Settings", Icons.Default.Settings)
}

val bottomNavScreens = listOf(
    Routes.Home,
    Routes.Recipe,
    Routes.Settings
)

//• DONE Home screen: list of recipe names (use LazyColumn)
//• DONE Detail screen: displays full recipe (title, ingredients, steps) using data passed via arguments
//• DONE Add Recipe screen: form for entering new recipe (basic state management)
//• DONE Use NavHostController, NavHost, and define a sealed Routes class
//• DONE Use navigate(route + "/{id}") to pass an argument and read it via backStackEntry
//• DONE? Use popUpTo() to control stack behavior when adding a new recipe
//• DONE Prevent multiple copies of the same screen using launchSingleTop
//• Style and layout using Scaffold and consistent navigation
//• Implement a BottomNavigation bar for “Home”, “Add”, and “Settings”; Persist recipes using in-memory state in ViewModel

// screens
@Composable
fun MyApp(viewModel: MyViewModel = viewModel()) {
    // create navController instance
    val navController = rememberNavController()

    // bottom bar + scaffold
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.title) }, // The text label for the item.
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title
                            )
                        }, // The icon for the item.

                        // 5. Determine if this item is currently selected.
                        // We check if the current route is part of the destination's hierarchy.
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,

                        // 6. Define the click action for the item.
                        onClick = {
                            // This is the core navigation logic.
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true // Save the state of the screen you're leaving.
                                }
                                // Avoid multiple copies of the same destination when re-selecting the same item.
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item.
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        innerPadding ->
        // 7. Define the NavHost, which is the container for our screen content.
        // The content of the NavHost changes based on the current route.
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route, // The first screen to show.
            modifier = Modifier.padding(innerPadding) // Apply padding from the Scaffold.
        ) {
            // Define a composable for each screen in our navigation graph.
            composable(route = "home") {
                HomeScreen(navController = navController, viewModel)
            }
            composable(Routes.Recipe.route) {
                RecipeScreen(navController = navController, viewModel)
            }
            composable(Routes.Settings.route) { GenericScreen(screen = Routes.Settings) }
            composable(Routes.Details.route + "/{title}/{ingredients}/{steps}") { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val ingredients = backStackEntry.arguments?.getString("ingredients") ?: ""
                val steps = backStackEntry.arguments?.getString("steps") ?: ""
                DetailsScreen(navController = navController, title, ingredients, steps)
            }
        }
    }

    // log changes if you want
//    LaunchedEffect(navController) {
//        navController.currentBackStack.collect { backStackEntries ->
//            val routeList = backStackEntries.joinToString(separator = " -> ") { it.destination.route ?: "null" }
//            Log.d(TAG, "Current Back Stack: $routeList")
//        }
//    }
    // create navhost -> container for all of my routes
//    NavHost(
//        navController = navController,
//        startDestination = "home"
//    ) {
//        // where the actual composables are defined with their route
//        composable(route = "home") {
//            HomeScreen(navController = navController, viewModel)
//        }
//
////        composable(route = "details") {
////            DetailsScreen(navController = navController)
////        }
//        composable(Routes.Details.route + "/{title}/{ingredients}/{steps}") { backStackEntry ->
//            val title = backStackEntry.arguments?.getString("title") ?: ""
//            val ingredients = backStackEntry.arguments?.getString("ingredients") ?: ""
//            val steps = backStackEntry.arguments?.getString("steps") ?: ""
//            DetailsScreen(navController = navController, title, ingredients, steps)
//        }
////
//        composable(Routes.Recipe.route) {
//            RecipeScreen(navController = navController, viewModel)
//        }
//    }
}

@Composable
fun GenericScreen(screen: Routes) {
    // Box is a simple layout composable that can be used to stack or align its children.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Center the content horizontally and vertically.
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = screen.icon, contentDescription = null, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = screen.title, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

// actually create the screens now
@Composable
fun HomeScreen(navController : NavController, viewModel: MyViewModel ) {
    val placeholders = listOf(
        "hello",
        "hello again",
        "goodbye"
    )
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()

    ) {
        Text(text = "Home")
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(viewModel.recipes) { index, recipe ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.LightGray
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = recipe.title) // justt the title, using our object
                    Button(onClick = {
                        navController.navigate(
                            "details/${recipe.title}/${recipe.ingredients}/${recipe.steps}" // pass the args
                        )
                        },
                        modifier = Modifier.padding(16.dp)) {
                        Text(text = "view details")
                    }
                }
            }
        }
        Button(
            onClick = {
                navController.navigate("recipe") {
                    launchSingleTop = true // only need one recipe screen
                }
            }
        ) {
            Text(text="add a recipe!")
        }
    }
}

// details screen
@Composable
fun DetailsScreen(navController : NavController, title : String,ingredients : String, steps : String) {
    Column(
        modifier = Modifier
            .padding(24.dp)
    ) {
        Text(text="Details")
        Text(text="title: $title")
        Text(text="ingredients $ingredients")
        Text(text="steps: $steps")
        Button(
            onClick = {
                // To go back, we can call `navController.navigateUp()`.
                // This "pops" the current screen (DetailsScreen) off the back stack,
                // taking the user back to the previous screen (HomeScreen).
                // This method is part of the NavController class itself and requires no special import.
//                Log.d(TAG, "Navigating back from Details to Home...")
                navController.navigateUp()
            }
        ) {
            Text("Go Back To Home")
        }
    }

}

//recipes screen
@Composable
fun RecipeScreen(navController : NavController, viewModel: MyViewModel) {
    var inputTitle by rememberSaveable { mutableStateOf("") }
    var inputIngredients by rememberSaveable { mutableStateOf("") }
    var inputSteps by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(24.dp)) {
            Text(text = "Input your recipe's title")
            TextField(
                value = inputTitle,
                onValueChange = { inputTitle = it },
                placeholder = { Text(text = "enter here...") }
            )
            Text(text = "Input your recipe's ingredients")
            TextField(
                value = inputIngredients,
                onValueChange = { inputIngredients = it },
                placeholder = { Text(text = "enter here...") }
            )
            Text(text = "Input your recipe's steps")
            TextField(
                value = inputSteps,
                onValueChange = { inputSteps = it },
                placeholder = { Text(text = "enter here...") }
            )

            Button(
                onClick = {
                    viewModel.addRecipe(
                        Recipe(inputTitle, inputIngredients, inputSteps)
                    )
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = false } // go back to Home
                        launchSingleTop = true // prevent duplicate HomeScreen
                    }
                },
                modifier = Modifier.padding(top = 16.dp)) {
                Text(text = "add recipe")
            }
        }
}