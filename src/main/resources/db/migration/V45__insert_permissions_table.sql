INSERT IGNORE INTO `permissions` (`permission`, `permission_for`) VALUES

-- Dashboard
('dashboard.count', 'Dashboard'),

-- Groups / Roles
('group.all', 'Group'),
('group.permission.all', 'Group'),
('group.permission.edit', 'Group'),
('group.detail', 'Group'),
('group.delete', 'Group'),

-- Items & Related
('item.all', 'Item'),
('item.detail', 'Item'),
('item.edit', 'Item'),
('item.import', 'Item'),
('item.export', 'Item'),
('item.delete', 'Item'),
('item.stock', 'Item'),
('item.status', 'Item'),
('item.image', 'Item'),
('item.category', 'Item'),
('item.category.delete', 'Item Category'),
('item.subcategory', 'Item Subcategory'),
('item.subcategory.edit', 'Item Subcategory'),
('item.subcategory.delete', 'Item Subcategory'),
('item.measuring.unit', 'Measuring Unit'),
('item.report.all', 'Item Report'),
('item.review.all', 'Item Review'),

-- Pagination
('pagination.all', 'Pagination'),

-- User Plans & Service Plans
('user.plan.detail', 'User Plan'),
('user.plan.all', 'User Plan'),
('service-plans.all', 'Service Plan'),
('service-plans.add', 'Service Plan'),
('service-plans.status.update', 'Service Plan'),
('service-plans.delete', 'Service Plan'),

-- Stores
('store.all', 'Store'),
('store.delete', 'Store'),
('store.detail', 'Store'),
('store.user', 'Store'),
('store.edit', 'Store'),
('store.profile.edit', 'Store'),
('store.status', 'Store'),
('store.profile', 'Store'),
('store.category.edit', 'Store Category'),
('store.category.detail', 'Store Category'),
('store.category.delete', 'Store Category'),
('store.subcategory.all', 'Store Subcategory'),
('store.subcategory.delete', 'Store Subcategory'),
('store.subcategory.edit', 'Store Subcategory'),
('store.report.all', 'Store Report'),

-- Wallet
('wallet.transactions', 'Wallet'),
('wallet.detail', 'Wallet'),
('wallet.pay', 'Wallet'),

-- Users
('user.all', 'User'),
('user.edit', 'User'),
('user.detail', 'User'),
('user.delete', 'User'),
('user.reset.password', 'User'),
('user.status', 'User'),
('user.profile.edit', 'User Profile'),
('user.profile', 'User Profile'),
('user.groups', 'User'),

-- Wholesaler
('wholesaler.permission', 'Wholesaler'),
('wholesaler.permission.update', 'Wholesaler');