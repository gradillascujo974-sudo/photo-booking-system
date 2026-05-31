CREATE TABLE IF NOT EXISTS sys_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL,
    name VARCHAR(64),
    phone VARCHAR(32),
    email VARCHAR(128),
    avatar VARCHAR(512),
    account DECIMAL(12,2) DEFAULT 0,
    introduce VARCHAR(512),
    city VARCHAR(64),
    styles VARCHAR(256),
    certified INT DEFAULT 0,
    rating DECIMAL(3,1) DEFAULT 5.0,
    wechat VARCHAR(128),
    pay_qrcode VARCHAR(512)
);

ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS wechat VARCHAR(128);
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS pay_qrcode VARCHAR(512);

CREATE TABLE IF NOT EXISTS photo_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS photo_service (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    descr VARCHAR(512),
    content CLOB,
    img VARCHAR(512),
    price DECIMAL(12,2) NOT NULL,
    style VARCHAR(64),
    tags VARCHAR(128),
    type_id INT,
    photographer_id INT NOT NULL,
    read_count INT DEFAULT 0,
    sale_count INT DEFAULT 0,
    status VARCHAR(32) DEFAULT '上架'
);

CREATE TABLE IF NOT EXISTS portfolio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    descr VARCHAR(512),
    img VARCHAR(512),
    photographer_id INT NOT NULL,
    read_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    status VARCHAR(32) DEFAULT '上架',
    sort_order INT DEFAULT 0,
    time VARCHAR(32)
);

ALTER TABLE portfolio ADD COLUMN IF NOT EXISTS status VARCHAR(32) DEFAULT '上架';
ALTER TABLE portfolio ADD COLUMN IF NOT EXISTS sort_order INT DEFAULT 0;
ALTER TABLE portfolio ADD COLUMN IF NOT EXISTS time VARCHAR(32);
UPDATE portfolio SET status = '上架' WHERE status IS NULL;
UPDATE portfolio SET sort_order = 0 WHERE sort_order IS NULL;

CREATE TABLE IF NOT EXISTS banner (
    id INT AUTO_INCREMENT PRIMARY KEY,
    img VARCHAR(512),
    server_id INT
);

CREATE TABLE IF NOT EXISTS notice (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    content CLOB,
    time VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS service_order (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    photographer_id INT NOT NULL,
    server_id INT NOT NULL,
    server_name VARCHAR(128),
    total DECIMAL(12,2) NOT NULL,
    server_time VARCHAR(64),
    phone VARCHAR(32),
    remark VARCHAR(512),
    status VARCHAR(32) NOT NULL,
    time VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS likes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id INT NOT NULL,
    user_id INT NOT NULL,
    time VARCHAR(32),
    CONSTRAINT uk_like UNIQUE (portfolio_id, user_id)
);

CREATE TABLE IF NOT EXISTS favorite (
    id INT AUTO_INCREMENT PRIMARY KEY,
    server_id INT NOT NULL,
    user_id INT NOT NULL,
    time VARCHAR(32),
    CONSTRAINT uk_fav UNIQUE (server_id, user_id)
);

CREATE TABLE IF NOT EXISTS comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    user_id INT NOT NULL,
    user_name VARCHAR(64),
    server_id INT,
    photographer_id INT,
    score DOUBLE,
    content VARCHAR(1024),
    time VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS photographer_identify (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    user_name VARCHAR(64),
    card VARCHAR(512),
    card_no VARCHAR(32),
    img VARCHAR(512),
    intro VARCHAR(512),
    status VARCHAR(32) DEFAULT '待审核',
    reason VARCHAR(512),
    time VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS media_file (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    filename VARCHAR(256),
    content_type VARCHAR(128) NOT NULL,
    data BLOB NOT NULL,
    size INT DEFAULT 0,
    time VARCHAR(32)
);
