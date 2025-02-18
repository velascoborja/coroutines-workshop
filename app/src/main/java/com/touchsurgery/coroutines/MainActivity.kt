package com.touchsurgery.coroutines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touchsurgery.coroutines.ui.theme.CoroutinesTheme

class MainActivity : ComponentActivity() {

    /* ViewModel in order to be used as a modern approach */
    private val viewModel by viewModels<MainViewModel>()

    /* Presenter using RXJava */
    private val presenter: MainPresenter = MainPresenter()

    /* Legacy presenter using coroutines, this could be used as an example for
    * classes that need some scope but are not viewModels or views */
    private val presenterWithCoroutines: CoroutinePresenter = CoroutinePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoroutinesTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CoroutinesButtons()
                    Spacer(Modifier.height(42.dp))
                    RxButtons()
                }
            }
        }
    }

    @Composable
    private fun CoroutinesButtons() {
        Text("Coroutines Version")

        Button(onClick = {
            viewModel.loadDataSequentially()
        }) {
            Text("Load data sequentially")
        }

        Button(onClick = {
            viewModel.loadDataInParallel()
        }) {
            Text("Load data in parallel")
        }

        Button(onClick = {
            viewModel.loadDataAndWait()
        }) {
            Text("Load data and wait for the slowest one")
        }

        Button(onClick = {
            viewModel.loadDataAndAsync()
        }) {
            Text("Load data and wait for each result")
        }

        Button(onClick = {
            viewModel.loadDataOnADifferentThread()
        }) {
            Text("Load data diff thread")
        }

        Button(onClick = {
            viewModel.loadDataOnADifferentThreadOption2()
        }) {
            Text("Load data diff thread option 2")
        }
    }

    @Composable
    private fun RxButtons() {
        Text("RX Version")

        Button(onClick = {
            presenter.loadDataSequentially()
        }) {
            Text("Load data sequentially")
        }

        Button(onClick = {
            presenter.loadDataInParallel()
        }) {
            Text("Load data in parallel")
        }

        Button(onClick = {
            presenter.loadDataAndWait()
        }) {
            Text("Load data and wait for the slowest one")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        /* This doesn't need to be done using coroutines as the scope is attached to
        * the viewModelScope */
        presenter.dispose()
        presenterWithCoroutines.onDestroy()
    }
}