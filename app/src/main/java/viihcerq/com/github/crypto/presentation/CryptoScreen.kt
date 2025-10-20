package viihcerq.com.github.crypto.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import viihcerq.com.github.crypto.model.Ticker
import viihcerq.com.github.crypto.presentation.viewModel.CryptoUiState
import viihcerq.com.github.crypto.presentation.viewModel.CryptoViewModel

@Composable
fun getCustomColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(0xFF007bff), // Azul primário
        onPrimary = Color.White,
        surface = Color(0xFF1E1E1E), // Fundo escuro
        background = Color(0xFF121212)
    )
}

@Composable
fun CryptoApp() {
    // Certifique-se de que a MainActivity use o setContent { CryptoApp() }
    val viewModel: CryptoViewModel = viewModel()

    MaterialTheme(colorScheme = getCustomColorScheme()) {
        CryptoScreen(
            state = viewModel.uiState.collectAsState().value,
            onRefreshClicked = viewModel::fetchTicker
        )
    }
}

@Composable
fun CryptoScreen(state: CryptoUiState, onRefreshClicked: () -> Unit) {
    Scaffold(
        topBar = { MainToolbar() },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (state) {
                is CryptoUiState.Loading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text("Carregando cotação...", modifier = Modifier.padding(top = 16.dp), color = Color.Gray)
                }
                is CryptoUiState.Success -> {
                    QuoteInformation(
                        ticker = state.ticker,
                        formattedDate = state.formattedDate,
                        onRefreshClicked = onRefreshClicked
                    )
                }
                is CryptoUiState.Error -> {
                    Text("Falha ao carregar os dados. Verifique sua conexão.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(24.dp))
                    RefreshButton(onRefreshClicked = onRefreshClicked)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainToolbar() {
    TopAppBar(
        title = {
            Text(
                text = "Monitor Bitcoin (BTC)",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun QuoteInformation(
    ticker: Ticker,
    formattedDate: String,
    onRefreshClicked: () -> Unit
) {
    // Usamos o viewModel() aqui para acessar a função de formatação do ViewModel.
    val viewModel: CryptoViewModel = viewModel()
    val formattedLast = remember(ticker.last) { viewModel.formatCurrency(ticker.last) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Última Cotação (BTC/BRL)",
            fontSize = 20.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = formattedLast,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Atualizado em: $formattedDate",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        RefreshButton(onRefreshClicked = onRefreshClicked)
    }
}


@Composable
fun RefreshButton(onRefreshClicked: () -> Unit) {
    Button(
        onClick = onRefreshClicked,
        modifier = Modifier
            .width(200.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Atualizar",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "ATUALIZAR", fontWeight = FontWeight.SemiBold)
    }
}