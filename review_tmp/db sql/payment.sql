            create table payment
                (
                  payment_id                      bigint           not null,
                  account_id                      bigint           not null,
                  payment_type_id                 smallint         not null,
                  currency_iso_num                smallint         not null,
                  net_amount                      money            not null,
                  country_type_id                 int              not null,
                  payment_method_id               bigint           not null,
                  payment_provider_id             int              not null,
                  merchant_account_id             int              not null,
                  payment_status_id               smallint         not null,
                  tracking_uuid                   uuid                 null,
                  date_created                    date             not null,
                  created_by                      varchar(50)          null,
                  date_updated                    date             not null,
                  updated_by                      varchar(50)          null,

                  constraint payment_id_pk primary key (payment_id),
                  constraint country_type_id_fk foreign key (country_type_id)
                      references country_type (country_type_id) match simple
                      on update no action on delete no action,
                  constraint currency_iso_num_fk foreign key (currency_iso_num)
                      references currency_type (currency_iso_num) match simple
                      on update no action on delete no action,
                  constraint merchant_account_id_fk foreign key (merchant_account_id)
                      references merchant_account (merchant_account_id) match simple
                      on update no action on delete no action,
                  constraint payment_provider_id_fk foreign key (payment_provider_id)
                      references payment_provider (payment_provider_id) match simple
                      on update no action on delete no action,
                  constraint payment_status_id_fk foreign key (payment_status_id)
                      references payment_status (payment_status_id) match simple
                      on update no action on delete no action,
                  constraint payment_type_id_fk foreign key (payment_type_id)
                      references payment_type (payment_type_id) match simple
                      on update no action on delete no action
                );
				
				create table payment_event
                (
                  payment_id                 bigint           not null,
                  payment_event_id           int              not null,
                  payment_event_type_id      smallint         not null,
                  event_date                 date             not null,
                  currency_iso_num           int              not null,
                  net_amount                 money            not null,
                  created_by                 varchar(50)          null,

                  constraint payment_id_event_id_pk primary key (payment_id, payment_event_id),
                  constraint payment_id_fk foreign key (payment_id)
                      references payment (payment_id) match simple
                      on update no action on delete no action
                );
				
				            create table country_type
                (
                  country_type_id                 int           not null,
                  iso_3166_country_number         int           not null,
                  iso_3166_country_code           char(2)       not null,
                  iso_3166_country_3_code         char(3)       not null,
                  iso_3166_country_name           varchar(128)  not null,
                  phone_country_code              varchar(8)        null,
                  default_currency_id             smallint          null,
                  country_vat_code                char(2)           null,
                  date_created                    date          not null,
                  created_by                      varchar(50)       null,

                  constraint country_type_id_pk primary key (country_type_id)
                );
				
				            create table phone_type
                (
                  phone_type_id         smallint            not null,
                  description           varchar(10)             null,
                  date_created          date                    null,
                  created_by            varchar(50)             null,

                  constraint phone_type_id_pk primary key (phone_type_id)
                );
				
				            create table payment_method_type
                (
                  payment_method_type_id        smallint            not null,
                  description                   varchar(50)         not null,
                  date_created                  date                not null,
                  created_by                    varchar(50)         not null,

                  constraint payment_method_type_id_pk primary key (payment_method_type_id)
                );
				
				            create table credit_card_type
                (
                  credit_card_type_id           smallint            not null,
                  description                   varchar(20)         not null,
                  display_name                  varchar             not null,
                  date_created                  date                not null,
                  created_by                    varchar(50)             null,

                  constraint credit_card_type_id_pk primary key (credit_card_type_id)
                );
				
				            create table address
                (
                  address_id                bigint              not null,
                  unit_number               varchar(10)             null,
                  address_line_1            varchar(100)        not null,
                  address_line_2            char(100)               null,
                  address_line_3            varchar(100)            null,
                  city                      varchar(50)             null,
                  district                  char(50)                null,
                  state                     varchar(50)             null,
                  country_type_id           int                 not null,
                  postal_code               varchar(50)         not null,
                  date_created              date                not null,
                  date_updated              date                    null,
                  updated_by                varchar(50)             null,

                  constraint address_id_pk primary key (address_id),
                  constraint country_type_id_fk foreign key (country_type_id)
                      references country_type (country_type_id) match simple
                      on update no action on delete no action
                );
				
				
				            create table phone
            (
              phone_id                  bigint              not null,
              phone_type_id             smallint            not null,
              country_type_id           smallint            not null,
              phone_area_code           varchar(10)             null,
              phone_local_number        varchar(15)         not null,
              phone_extension           varchar(10)             null,
              date_created              date                not null,
              date_updated              date                    null,
              updated_by                varchar(50)             null,

              constraint phone_id_pk primary key (phone_id),
              constraint country_type_id_phone_fk foreign key (country_type_id)
                  references country_type (country_type_id) match simple
                  on update no action on delete no action,
              constraint phone_type_id_fk foreign key (phone_type_id)
                  references phone_type (phone_type_id) match simple
                  on update no action on delete no action
            );
			
			
			            create table payment_method
                (
                  payment_method_id                 bigint              not null,
                  account_id                        bigint              not null,
                  payment_method_type_id            smallint            not null,
                  payment_method_holder_name        varchar(100)        not null,
                  encrypted_account_number          varchar(200)            null,
                  display_account_number            varchar(100)            null,
                  address_id                        bigint              not null,
                  phone_id                          bigint                  null,
                  date_created                      date                not null,
                  date_updated                      date                    null, 
                  updated_by                        varchar(50)             null,

                  constraint payment_method_id_pk primary key (payment_method_id),
                  constraint address_id_fk foreign key (address_id)
                      references address (address_id) match simple
                      on update no action on delete no action,
                  constraint payment_method_type_id_fk foreign key (payment_method_type_id)
                      references payment_method_type (payment_method_type_id) match simple
                      on update no action on delete no action,
                  constraint phone_id_fk foreign key (phone_id)
                      references phone (phone_id) match simple
                      on update no action on delete no action
                );
				
				
				            create table credit_card_payment_method
                (
                  payment_method_id                 bigint              not null,
                  credit_card_type_id               smallint            not null,
                  expire_date                       date                not null,
                  last_billing_date                 date                not null,

                  constraint cc_payment_method_id_pk primary key (payment_method_id),
                  constraint credit_card_type_id_fk foreign key (credit_card_type_id)
                      references credit_card_type (credit_card_type_id) match simple
                      on update no action on delete no action
                );