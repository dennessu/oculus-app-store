            create table category (
              category_id            bigint             not null,
              parent_id              bigint             default null,
              name                   varchar(64)        not null,
              type                   varchar(64)        not null,
              payload                json               not null,
              created_by             varchar(64)        not null,
              created_date           timestamp          not null,
              updated_by             varchar(64)        default null,
              updated_date           timestamp          default null,

              primary key (category_id)
            );

            create table item (
              item_id                 bigint            not null,
              name                    varchar(64)       not null,
              type                    varchar(64)       not null,
              category_id             bigint            not null,
              owner_id                bigint            not null,
              payload                 json              not null,
              created_by              varchar(64)       not null,
              created_date            timestamp         not null,
              updated_by              varchar(64)       default null,
              updated_date            timestamp         default null,

              primary key (item_id)
            );

            create table item_revision (
              item_id                 bigint            not null,
              revision                bigint            not null,
              name                    varchar(64)       not null,
              type                    varchar(64)       not null,
              category_id             bigint            not null,
              owner_id                bigint            not null,
              payload                 json              not null,
              created_by              varchar(64)       not null,
              created_date            timestamp         not null,
              updated_by              varchar(64)       default null,
              updated_date            timestamp         default null,

              primary key (item_id, revision)
            );