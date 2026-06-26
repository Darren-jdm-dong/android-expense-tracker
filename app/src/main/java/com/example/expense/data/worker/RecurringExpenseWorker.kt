package com.example.expense.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expense.domain.usecase.ManageRecurringUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RecurringExpenseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val manageRecurringUseCase: ManageRecurringUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val generatedIds = manageRecurringUseCase.generateDueExpenses()
            if (generatedIds.isNotEmpty()) {
                // Could send notification here
                Result.success()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "recurring_expense_worker"
    }
}
