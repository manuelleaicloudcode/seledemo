package com.lnxjuantous.qa.ccmbers;

import org.testng.annotations.DataProvider;
import java.util.*;

public class FundTransferDataProvider {

	// Centralized query repository for fund transfers
	private static final Map<String, List<String>> FUND_TRANSFER_QUERIES = new HashMap<>();

	// Initialize the query repository
	static {
		// Account validation queries
		FUND_TRANSFER_QUERIES.put("account_validation",
				Arrays.asList("SELECT account_id, balance, status FROM accounts WHERE account_id = ?",
						"SELECT COUNT(*) FROM accounts WHERE account_id = ? AND status = 'active'",
						"SELECT account_id, user_id FROM accounts WHERE user_id = ?"));

		// Fund transfer queries
		FUND_TRANSFER_QUERIES.put("fund_transfer", Arrays.asList(
				"UPDATE accounts SET balance = balance - ? WHERE account_id = ?",
				"UPDATE accounts SET balance = balance + ? WHERE account_id = ?",
				"INSERT INTO transactions (from_account, to_account, amount, transaction_date, status) VALUES (?, ?, ?, NOW(), 'completed')"));

		// Transaction history queries
		FUND_TRANSFER_QUERIES.put("transaction_history", Arrays.asList(
				"SELECT t.transaction_id, t.from_account, t.to_account, t.amount, t.transaction_date, t.status FROM transactions t WHERE t.from_account = ? OR t.to_account = ? ORDER BY t.transaction_date DESC",
				"SELECT COUNT(*) FROM transactions WHERE from_account = ? AND transaction_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)",
				"SELECT SUM(amount) FROM transactions WHERE from_account = ? AND status = 'completed' AND transaction_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)"));
	}

	// Data provider for account validation queries
	@DataProvider(name = "accountValidationProvider")
	public static Object[][] accountValidationProvider() {
		return getQueriesForCategory("account_validation");
	}

	// Data provider for fund transfer queries
	@DataProvider(name = "fundTransferProvider")
	public static Object[][] fundTransferProvider() {
		return getQueriesForCategory("fund_transfer");
	}

	// Data provider for transaction history queries
	@DataProvider(name = "transactionHistoryProvider")
	public static Object[][] transactionHistoryProvider() {
		return getQueriesForCategory("transaction_history");
	}

	// Helper method to get queries for a specific category
	private static Object[][] getQueriesForCategory(String category) {
		List<String> queries = FUND_TRANSFER_QUERIES.get(category);
		if (queries == null || queries.isEmpty()) {
			throw new IllegalArgumentException("No queries found for category: " + category);
		}

		Object[][] result = new Object[queries.size()][1];
		for (int i = 0; i < queries.size(); i++) {
			result[i][0] = validateQuery(queries.get(i));
		}

		return result;
	}

	// Query validation method
	private static String validateQuery(String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new IllegalArgumentException("Invalid query: Query cannot be null or empty");
		}
		return query.trim();
	}

	// Method to get specific query by category and index
	public static String getQuery(String category, int index) {
		List<String> queries = FUND_TRANSFER_QUERIES.get(category);
		if (queries == null || queries.isEmpty() || index < 0 || index >= queries.size()) {
			throw new IllegalArgumentException("Invalid query index or category");
		}
		return validateQuery(queries.get(index));
	}
}