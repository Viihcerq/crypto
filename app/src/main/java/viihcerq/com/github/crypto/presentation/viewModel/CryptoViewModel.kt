package viihcerq.com.github.crypto.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import viihcerq.com.github.crypto.model.Ticker
import viihcerq.com.github.crypto.service.MercadoBitcoinServiceFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
sealed class CryptoUiState {
    object Loading : CryptoUiState()
    data class Success(val ticker: Ticker, val formattedDate: String) : CryptoUiState()
    object Error : CryptoUiState()
}

class CryptoViewModel : ViewModel() {

    private val service = MercadoBitcoinServiceFactory().create()

    private val _uiState = MutableStateFlow<CryptoUiState>(CryptoUiState.Loading)
    val uiState: StateFlow<CryptoUiState> = _uiState

    init {
        fetchTicker()
    }

    fun fetchTicker() {
        _uiState.value = CryptoUiState.Loading
        viewModelScope.launch {
            try {
                val response = service.getTicker()
                if (response.isSuccessful && response.body() != null) {
                    val ticker = response.body()!!.ticker
                    val formattedDate = formatDate(ticker.date)
                    _uiState.value = CryptoUiState.Success(ticker, formattedDate)
                } else {
                    _uiState.value = CryptoUiState.Error
                }
            } catch (e: Exception) {
                println("Error fetching ticker: ${e.message}")
                _uiState.value = CryptoUiState.Error
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt", "BR"))
        return format.format(date)
    }

    fun formatCurrency(value: String): String {
        return try {
            val number = value.toDouble()
            val formatter = DecimalFormat("R$ #,##0.00")
            formatter.format(number)
        } catch (e: NumberFormatException) {
            "R$ 0,00"
        }
    }
}