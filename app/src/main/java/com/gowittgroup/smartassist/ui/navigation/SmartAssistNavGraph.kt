package com.gowittgroup.smartassist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gowittgroup.smartassist.ui.aboutscreen.AboutScreen
import com.gowittgroup.smartassist.ui.aboutscreen.AboutViewModel
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.auth.SignInScreen
import com.gowittgroup.smartassist.ui.auth.SignInViewModel
import com.gowittgroup.smartassist.ui.auth.SignUpScreen
import com.gowittgroup.smartassist.ui.auth.SignUpViewModel
import com.gowittgroup.smartassist.ui.faqscreen.FaqScreen
import com.gowittgroup.smartassist.ui.faqscreen.FaqViewModel
import com.gowittgroup.smartassist.ui.history.HistoryScreen
import com.gowittgroup.smartassist.ui.history.HistoryViewModel
import com.gowittgroup.smartassist.ui.homescreen.HomeScreen
import com.gowittgroup.smartassist.ui.homescreen.HomeViewModel
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreen
import com.gowittgroup.smartassist.ui.promptscreen.PromptsViewModel
import com.gowittgroup.smartassist.ui.settingsscreen.SettingsScreen
import com.gowittgroup.smartassist.ui.settingsscreen.SettingsViewModel
import com.gowittgroup.smartassist.ui.splashscreen.SplashScreen


@Composable
fun SmartAssistNavGraph(
    smartAnalytics: SmartAnalytics,
    openDrawer: () -> Unit,
    isExpandedScreen: Boolean,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SmartAssistDestinations.SPLASH_ROUTE
) {
    val navigationActions: SmartAssistNavigationActions =
        remember { SmartAssistNavigationActions(navController) }
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {

        composable(SmartAssistDestinations.SPLASH_ROUTE) {
            SplashScreen(
                navigateToHome = navigationActions.navigateToHome,
                navigateToSignUp = navigationActions.navigateToSignUp,
                navigateToSignIn = navigationActions.navigateToSignIn
            )
        }

        composable(SmartAssistDestinations.SIGN_IN) {
            val signInViewModel: SignInViewModel = hiltViewModel()
            val uiState by signInViewModel.uiState.collectAsState()
            val currentUser by signInViewModel.currentUser.collectAsState()
            SignInScreen(
                uiState = uiState,
                currentUser = currentUser,
                navigateToHome = navigationActions.navigateToHome,
                onEmailChange = signInViewModel::updateEmail,
                onPasswordChange = signInViewModel::updatePassword,
                onSignInClick = signInViewModel::onSignInClick,
                navigateToSignUp = navigationActions.navigateToSignUp
            )
        }

        composable(SmartAssistDestinations.SIGN_UP) {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            val uiState by signUpViewModel.uiState.collectAsState()
            val currentUser by signUpViewModel.currentUser.collectAsState()
            SignUpScreen(
                uiState = uiState,
                currentUser = currentUser,
                onEmailChange = signUpViewModel::updateEmail,
                onPasswordChange = signUpViewModel::updatePassword,
                onConfirmPasswordChange = signUpViewModel::updateConfirmPassword,
                onSignUpClick = signUpViewModel::onSignUpClick,
                navigateToSignIn = navigationActions.navigateToSignIn,
                navigateToHome = navigationActions.navigateToHome
            )
        }

        composable(route = SmartAssistDestinations.HOME_ROUTE + "/{id}/{prompt}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument("prompt") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val uiState by homeViewModel.uiState.observeAsState()
            HomeScreen(
                uiState = uiState!!,
                openDrawer = openDrawer,
                showTopAppBar = !isExpandedScreen,
                isExpanded = isExpandedScreen,
                navigateToSettings = navigationActions.navigateToSettings,
                navigateToHistory = navigationActions.navigateToHistory,
                navigateToPrompts = navigationActions.navigateToPrompts,
                navigateToHome = navigationActions.navigateToHome,
                smartAnalytics = smartAnalytics,
                ask = homeViewModel::ask,
                beginningSpeech = homeViewModel::beginningSpeech,
                setCommandModeAfterReply = homeViewModel::setCommandModeAfterReply,
                handsFreeModeStopListening = homeViewModel::handsFreeModeStopListening,
                setCommandMode = homeViewModel::setCommandMode,
                releaseCommandMode = homeViewModel::releaseCommandMode,
                handsFreeModeStartListening = homeViewModel::handsFreeModeStartListening,
                resetErrorMessage = homeViewModel::resetErrorMessage,
                setReadAloud = homeViewModel::setReadAloud,
                closeHandsFreeAlert = homeViewModel::closeHandsFreeAlert,
                setHandsFreeMode = homeViewModel::setHandsFreeMode,
                stopListening = homeViewModel::stopListening,
                startListening = homeViewModel::startListening,
                updateHint = homeViewModel::updateHint,
                refreshAll = homeViewModel::refreshAll
            )

        }
        composable(SmartAssistDestinations.HISTORY_ROUTE) {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            val uiState by historyViewModel.uiState.collectAsState()
            HistoryScreen(
                uiState = uiState,
                isExpanded = isExpandedScreen,
                openDrawer = openDrawer,
                navigateToHome = navigationActions.navigateToHome,
                smartAnalytics = smartAnalytics,
                deleteHistory = historyViewModel::deleteHistory,
                onQueryChange = historyViewModel::search
            )
        }
        composable(SmartAssistDestinations.SETTINGS_ROUTE) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val uiState by settingsViewModel.uiState.collectAsState()
            val currentUser by settingsViewModel.currentUser.collectAsState()
            SettingsScreen(
                uiState = uiState,
                currentUser = currentUser,
                isExpanded = isExpandedScreen,
                openDrawer = openDrawer,
                smartAnalytics = smartAnalytics,
                refreshErrorMessage = settingsViewModel::resetErrorMessage,
                toggleReadAloud = settingsViewModel::toggleReadAloud,
                toggleHandsFreeMode = settingsViewModel::toggleHandsFreeMode,
                chooseAiTool = settingsViewModel::chooseAiTool,
                chooseChatModel = settingsViewModel::chooseChatModel,
                onLogout = settingsViewModel::logout,
                navigateToSignIn = navigationActions.navigateToSignIn
            )
        }

        composable(SmartAssistDestinations.PROMPTS_ROUTE) {
            val promptsViewModel: PromptsViewModel = hiltViewModel()
            val uiState by promptsViewModel.uiState.collectAsState()
            PromptsScreen(
                uiState = uiState,
                isExpanded = isExpandedScreen,
                openDrawer = openDrawer,
                navigateToHome = navigationActions.navigateToHome,
                smartAnalytics = smartAnalytics,
                resetErrorMessage = promptsViewModel::resetErrorMessage
            )
        }

        composable(SmartAssistDestinations.ABOUT_ROUTE) {
            val aboutViewModel: AboutViewModel = hiltViewModel()
            val uiState by aboutViewModel.uiState.collectAsState()
            AboutScreen(
                uiState = uiState,
                isExpanded = isExpandedScreen,
                openDrawer = openDrawer,
                smartAnalytics = smartAnalytics,
                refreshErrorMessage = aboutViewModel::resetErrorMessage,
                navigateToFaq = navigationActions.navigateToFaq
            )
        }

        composable(SmartAssistDestinations.FAQ_ROUTE) {
            val faqViewModel: FaqViewModel = hiltViewModel()
            val uiState by faqViewModel.uiState.collectAsState()
            FaqScreen(
                uiState = uiState,
                isExpanded = isExpandedScreen,
                openDrawer = openDrawer,
                smartAnalytics = smartAnalytics,
                refreshErrorMessage = faqViewModel::resetErrorMessage
            )
        }
    }
}

