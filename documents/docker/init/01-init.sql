-- Folio DB 초기화

-- 스키마 생성
CREATE SCHEMA IF NOT EXISTS folio;

-- 기존 테이블 삭제 (FK 의존성 역순)
DROP TABLE IF EXISTS folio.account_transaction;
DROP TABLE IF EXISTS folio.account;
DROP TABLE IF EXISTS folio.menu;
DROP TABLE IF EXISTS folio.member;

-- 사용자 테이블
CREATE TABLE folio.member (
    id          BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,  -- 고유 식별자
    login_id    VARCHAR(50)  NOT NULL UNIQUE,                           -- 로그인 ID
    password    VARCHAR(255) NOT NULL,                                  -- 비밀번호 (BCrypt 해시)
    nickname    VARCHAR(30)  NOT NULL,                                  -- 닉네임
    email       VARCHAR(100) NOT NULL UNIQUE,                           -- 이메일
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',                   -- 권한 (USER / ADMIN)
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',                 -- 상태 (ACTIVE / INACTIVE / SUSPENDED)
    updated_at  TIMESTAMPTZ  DEFAULT NULL,                              -- 수정일 (최초 NULL, 수정 시 갱신)
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()                     -- 가입일
);

COMMENT ON TABLE  folio.member              IS '회원 정보';
COMMENT ON COLUMN folio.member.id           IS '고유 식별자';
COMMENT ON COLUMN folio.member.login_id     IS '로그인 ID';
COMMENT ON COLUMN folio.member.password     IS '비밀번호 (BCrypt 해시)';
COMMENT ON COLUMN folio.member.nickname     IS '닉네임';
COMMENT ON COLUMN folio.member.email        IS '이메일';
COMMENT ON COLUMN folio.member.role         IS '권한 (USER / ADMIN)';
COMMENT ON COLUMN folio.member.status       IS '상태 (ACTIVE / INACTIVE / SUSPENDED)';
COMMENT ON COLUMN folio.member.updated_at   IS '수정일';
COMMENT ON COLUMN folio.member.created_at   IS '가입일';

-- 인덱스
CREATE INDEX idx_member_login_id ON folio.member (login_id);
CREATE INDEX idx_member_email    ON folio.member (email);

-- 메뉴 테이블
CREATE TABLE folio.menu (
    id          BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,  -- 메뉴 ID
    parent_id   BIGINT       DEFAULT NULL,                              -- 상위 메뉴 ID (NULL = 최상위)
    menu_name   VARCHAR(50)  NOT NULL,                                  -- 메뉴 표시명
    menu_code   VARCHAR(50)  NOT NULL UNIQUE,                           -- 메뉴 코드 (식별용)
    menu_url    VARCHAR(200) DEFAULT NULL,                              -- 이동 URL (NULL = 그룹 메뉴)
    icon        TEXT         DEFAULT NULL,                              -- SVG 아이콘 (HTML 문자열)
    depth       SMALLINT     NOT NULL DEFAULT 1,                        -- 메뉴 뎁스 (1~3)
    sort_order  INT          NOT NULL DEFAULT 0,                        -- 정렬 순서 (같은 부모 내)
    visible     BOOLEAN      NOT NULL DEFAULT TRUE,                     -- 노출 여부
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),                    -- 생성일
    updated_at  TIMESTAMPTZ  DEFAULT NULL,                              -- 수정일

    CONSTRAINT fk_menu_parent FOREIGN KEY (parent_id) REFERENCES folio.menu (id) ON DELETE CASCADE,
    CONSTRAINT chk_menu_depth CHECK (depth BETWEEN 1 AND 3)
);

COMMENT ON TABLE  folio.menu              IS '메뉴 정보';
COMMENT ON COLUMN folio.menu.id           IS '메뉴 ID';
COMMENT ON COLUMN folio.menu.parent_id    IS '상위 메뉴 ID (NULL = 최상위)';
COMMENT ON COLUMN folio.menu.menu_name    IS '메뉴 표시명';
COMMENT ON COLUMN folio.menu.menu_code    IS '메뉴 코드 (식별용)';
COMMENT ON COLUMN folio.menu.menu_url     IS '이동 URL (NULL = 그룹 메뉴)';
COMMENT ON COLUMN folio.menu.icon         IS 'SVG 아이콘';
COMMENT ON COLUMN folio.menu.depth        IS '메뉴 뎁스 (1~3)';
COMMENT ON COLUMN folio.menu.sort_order   IS '정렬 순서';
COMMENT ON COLUMN folio.menu.visible      IS '노출 여부';
COMMENT ON COLUMN folio.menu.created_at   IS '생성일';
COMMENT ON COLUMN folio.menu.updated_at   IS '수정일';

-- 인덱스
CREATE INDEX idx_menu_parent_id ON folio.menu (parent_id);
CREATE INDEX idx_menu_sort      ON folio.menu (depth, sort_order);

-- 메뉴 초기 데이터
INSERT INTO folio.menu (parent_id, menu_name, menu_code, menu_url, icon, depth, sort_order) VALUES
(NULL, 'Dashboard',      'dashboard',       '/dashboard',       '<svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>', 1, 1),
(NULL, 'Accounts',       'accounts',        '/accounts',        '<svg viewBox="0 0 24 24"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>', 1, 2),
(NULL, 'Trade History',  'trading-history', '/trading-history', '<svg viewBox="0 0 24 24"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>', 1, 3),
(NULL, 'Community',      'community',       '/community',       '<svg viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>', 1, 4),
(NULL, 'Exchange Rate',  'exchange',        '/exchange',        '<svg viewBox="0 0 24 24"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>', 1, 5),
(NULL, 'Settings',       'settings',        '/settings',        '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>', 1, 6);

-- 계좌 테이블
CREATE TABLE folio.account (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,   -- 계좌 ID
    member_id       BIGINT          NOT NULL,                                   -- 소유자 (member FK)
    account_name    VARCHAR(100)    NOT NULL,                                   -- 계좌명 (예: "바이낸스 선물")
    broker          VARCHAR(50)     DEFAULT NULL,                               -- 브로커명 (선택)
    currency        VARCHAR(10)     NOT NULL DEFAULT 'USD',                     -- 통화 (KRW, USD, USDT 등)
    balance         NUMERIC(18,4)   NOT NULL DEFAULT 0,                         -- 잔고 (입출금으로 관리)
    updated_at      TIMESTAMPTZ     DEFAULT NULL,                               -- 수정일
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),                     -- 생성일

    CONSTRAINT fk_account_member FOREIGN KEY (member_id) REFERENCES folio.member (id) ON DELETE CASCADE
);

COMMENT ON TABLE  folio.account                  IS '계좌 정보';
COMMENT ON COLUMN folio.account.id               IS '계좌 ID';
COMMENT ON COLUMN folio.account.member_id        IS '소유자 (member FK, BIGINT)';
COMMENT ON COLUMN folio.account.account_name     IS '계좌명';
COMMENT ON COLUMN folio.account.broker           IS '브로커명';
COMMENT ON COLUMN folio.account.currency         IS '통화 (KRW, USD, USDT 등)';
COMMENT ON COLUMN folio.account.balance          IS '잔고 (입출금으로 관리)';
COMMENT ON COLUMN folio.account.updated_at       IS '수정일';
COMMENT ON COLUMN folio.account.created_at       IS '생성일';

-- 인덱스
CREATE INDEX idx_account_member_id ON folio.account (member_id);

-- 입출금 내역 테이블
CREATE TABLE folio.account_transaction (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,   -- 입출금 ID
    account_id      BIGINT          NOT NULL,                                   -- 계좌 FK
    type            VARCHAR(20)     NOT NULL,                                   -- DEPOSIT / WITHDRAWAL
    amount          NUMERIC(18,4)   NOT NULL,                                   -- 금액
    memo            VARCHAR(200)    DEFAULT NULL,                               -- 메모 (선택)
    transacted_at   TIMESTAMPTZ     NOT NULL,                                   -- 입출금 일시
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),                     -- 생성일

    CONSTRAINT fk_acct_tx_account FOREIGN KEY (account_id) REFERENCES folio.account (id) ON DELETE CASCADE,
    CONSTRAINT chk_acct_tx_type   CHECK (type IN ('DEPOSIT', 'WITHDRAWAL')),
    CONSTRAINT chk_acct_tx_amount CHECK (amount > 0)
);

COMMENT ON TABLE  folio.account_transaction                IS '입출금 내역';
COMMENT ON COLUMN folio.account_transaction.id             IS '입출금 ID';
COMMENT ON COLUMN folio.account_transaction.account_id     IS '계좌 FK';
COMMENT ON COLUMN folio.account_transaction.type           IS '유형 (DEPOSIT / WITHDRAWAL)';
COMMENT ON COLUMN folio.account_transaction.amount         IS '금액';
COMMENT ON COLUMN folio.account_transaction.memo           IS '메모';
COMMENT ON COLUMN folio.account_transaction.transacted_at  IS '입출금 일시';
COMMENT ON COLUMN folio.account_transaction.created_at     IS '생성일';

-- 인덱스
CREATE INDEX idx_acct_tx_account_id ON folio.account_transaction (account_id);
CREATE INDEX idx_acct_tx_transacted ON folio.account_transaction (transacted_at);
