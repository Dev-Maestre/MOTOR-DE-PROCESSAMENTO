CREATE TABLE event (
                       id UUID PRIMARY KEY,
                       external_id VARCHAR(255) NOT NULL UNIQUE,
                       type VARCHAR(100) NOT NULL,
                       source VARCHAR(100) NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       attempts INT NOT NULL DEFAULT 0,
                       next_attempt_at TIMESTAMP WITH TIME ZONE,
                       payload JSONB NOT NULL,
                       occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índice para acelerar a busca por status e next_attempt_at (essencial para o futuro Worker de polling)
CREATE INDEX idx_event_status_next_attempt ON event(status, next_attempt_at);
