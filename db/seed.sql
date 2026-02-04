USE store_db;

INSERT INTO sys_role (id, role_name, role_code) VALUES
  (1, '店长', 'STORE_MANAGER'),
  (2, '收银员', 'CASHIER'),
  (3, '仓库员', 'WAREHOUSE'),
  (4, '财务', 'FINANCE'),
  (5, '管理员', 'ADMIN')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), role_code=VALUES(role_code);

DELETE FROM sys_role_permission WHERE role_id IN (1,2,3,4,5);

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (1, 'STOCK_IN'),
  (1, 'STOCK_OUT'),
  (1, 'STOCK_EDIT'),
  (1, 'ACCOUNT_VIEW'),
  (1, 'USER_MGR'),
  (1, 'ROLE_MGR');

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (2, 'STOCK_OUT');

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (3, 'STOCK_IN');

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (4, 'ACCOUNT_VIEW');

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (5, 'USER_MGR'),
  (5, 'ROLE_MGR');

INSERT INTO sys_user (id, username, password, name, phone, role_id, status) VALUES
  (1, 'admin', '$2b$10$wNETgw6j55nPzYoiku51MeZ/mm8RgKH4luMxJ2LunFg7zOMnwtSQ.', '超级管理员', NULL, 1, 1)
ON DUPLICATE KEY UPDATE
  password=VALUES(password),
  name=VALUES(name),
  role_id=VALUES(role_id),
  status=VALUES(status);
