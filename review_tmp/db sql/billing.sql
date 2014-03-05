			CREATE TABLE balance (
				balance_id BIGINT NOT NULL,
				pi_id BIGINT NOT NULL,
				total_amount DECIMAL NOT NULL,
				tax_amount DECIMAL NOT NULL,
				tax_included BOOLEAN,
				currency_id SMALLINT NOT NULL,
				country_code CHAR(2),
				due_date DATE NOT NULL,
				retry_count INT NOT NULL DEFAULT(0),
				decline_count INT NOT NULL DEFAULT(0),
				requestor_id VARCHAR(64) NOT NULL,
				onbehalfof_requestor_od VARCHAR(64),
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(balance_id)
			);
			CREATE UNIQUE INDEX balance_pi_id_idx ON balance (pi_id);
			CREATE UNIQUE INDEX balance_due_date_idx ON balance (due_date);

			CREATE TABLE balance_event (
				balance_event_id BIGINT NOT NULL,
				balance_id BIGINT NOT NULL,
				balance_action_id SMALLINT NOT NULL,
				balance_status_id SMALLINT NOT NULL,
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(balance_event_id)
			);
			CREATE UNIQUE INDEX balance_event_balance_id_idx ON balance_event (balance_id);
			CREATE UNIQUE INDEX balance_event_balance_status_id_idx ON balance_event (balance_status_id);

			CREATE TABLE balance_item (
				balance_item_id BIGINT NOT NULL,
				balance_id BIGINT NOT NULL,
				order_item_id BIGINT NOT NULL,
				amount DECIMAL NOT NULL,
				tax_amount DECIMAL NOT NULL,
				tax_included BOOLEAN,
				finance_id VARCHAR(64),
				original_balance_item_id BIGINT,
				original_order_item_id BIGINT,
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(balance_item_id)
			);
			CREATE UNIQUE INDEX balance_item_balance_id_idx ON balance_item (balance_id);
			CREATE UNIQUE INDEX balance_item_order_item_id_idx ON balance_item (order_item_id);
			CREATE UNIQUE INDEX balance_item_original_balance_item_id_idx ON balance_item (original_balance_item_id);
			CREATE UNIQUE INDEX balance_item_original_order_item_id_idx ON balance_item (original_order_item_id);

			CREATE TABLE order_balance_link (
				order_balance_link_id BIGINT NOT NULL,
				order_id BIGINT NOT NULL,
				balance_id BIGINT NOT NULL,
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(order_balance_link_id)
			);
			CREATE UNIQUE INDEX order_balance_link_order_id_idx ON order_balance_link (order_id);
			CREATE UNIQUE INDEX order_balance_link_balance_id_idx ON order_balance_link (balance_id);

			CREATE TABLE tax_item (
				tax_item_id BIGINT NOT NULL,
				balance_item_id BIGINT NOT NULL,
				tax_authority_id SHORT NOT NULL,
				tax_amount DECIMAL NOT NULL,
				tax_rate DECIMAL,
				is_tax_exempt BOOLEAN,
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(tax_item_id)
			);
			CREATE UNIQUE INDEX tax_item_balance_item_id_idx ON tax_item (balance_item_id);

			CREATE TABLE discount_item (
				discount_item_id BIGINT NOT NULL,
				balance_item_id BIGINT NOT NULL,
				discount_amount DECIMAL NOT NULL,
				discount_rate DECIMAL,
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(tax_item_id)
			);
			CREATE UNIQUE INDEX discount_item_balance_item_id_idx ON discount_item (balance_item_id);
			
			CREATE TABLE shipping_address (
				shipping_address_id BIGINT NOT NULL,
				user_id BIGINT NOT NULL,
				street VARCHAR(512) NOT NULL,
				street2 VARCHAR(512),
				street3 VARCHAR(512),
				city VARCHAR(128) NOT NULL,
				state VARCHAR(128),
				postal_code VARCHAR(64) NOT NULL,
				country_code CHAR(2) NOT NULL,
				company_name VARCHAR(256),
				first_name VARCHAR(256) NOT NULL,
				middle_name VARCHAR(256),
				last_name VARCHAR(256) NOT NULL,
				phone_number VARCHAR(128),
				description VARCHAR(512),
				requestor_id VARCHAR(64) NOT NULL,
				onbehalfof_requestor_id VARCHAR(64),
				deleted BOOLEAN NOT NULL DEFAULT(FALSE),
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(shipping_address_id)
			);
			CREATE UNIQUE INDEX shipping_address_user_id_idx ON shipping_address (user_id);
			CREATE UNIQUE INDEX shipping_address_deleted_idx ON shipping_address (deleted);		

			CREATE TABLE billing_transaction (
				transaction_id BIGINT NOT NULL,
				balance_id BIGINT NOT NULL,
				transaction_type_id SMALLINT NOT NULL,
				payment_ref_id VARCHAR(128) NOT NULL,
				amount DECIMAL NOT NULL,
				currency_id SMALLINT NOT NULL,
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(transaction_id)
			);
			CREATE UNIQUE INDEX billing_transaction_balance_id_idx ON billing_transaction (balance_id);

			CREATE TABLE transaction_event (
				transaction_event_id BIGINT NOT NULL,
				transaction_id BIGINT NOT NULL,
				transaction_action_id SMALLINT NOT NULL,
				transaction_status_id SMALLINT NOT NULL,
				created_date TIMESTAMP NOT NULL,
				created_by VARCHAR(64) NOT NULL,
				modified_date TIMESTAMP,
				modified_by VARCHAR(64),
				PRIMARY KEY(transaction_event_id)
			);
			CREATE UNIQUE INDEX billing_transaction_event_transaction_id_idx ON billing_transaction (transaction_id);
			CREATE UNIQUE INDEX billing_transaction_event_transaction_status_id_idx ON billing_transaction (transaction_status_id);			