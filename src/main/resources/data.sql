-- Dados de exemplo para as aulas (recriados a cada execução)

INSERT INTO usuarios (nome, email, papel, ativo) VALUES
  ('Ana Gestora',     'gestor@ifrn.edu.br',     'GESTOR',    true),
  ('Bruno Servidor',  'servidor@ifrn.edu.br',   'SERVIDOR',  true),
  ('Carla Servidora', 'carla@ifrn.edu.br',      'SERVIDOR',  true),
  ('Diego Motorista', 'diego@terceirizada.com', 'MOTORISTA', true),
  ('Elias Motorista', 'elias@terceirizada.com', 'MOTORISTA', true),
  ('Fábio Motorista', 'fabio@terceirizada.com', 'MOTORISTA', true);

INSERT INTO veiculos (placa, marca, modelo, ano, tipo, capacidade_passageiros, quilometragem_atual, status) VALUES
  ('RNX1A23', 'Fiat',       'Cronos',   2022, 'CARRO',  4,  35210,  'DISPONIVEL'),
  ('RNY2B34', 'Chevrolet',  'Spin',     2021, 'CARRO',  6,  58900,  'DISPONIVEL'),
  ('QGA3C45', 'Renault',    'Master',   2020, 'VAN',    15, 102350, 'DISPONIVEL'),
  ('QGB4D56', 'Mercedes',   'OF-1519',  2018, 'ONIBUS', 44, 210400, 'DISPONIVEL'),
  ('OJK5E67', 'Volkswagen', 'Gol',      2014, 'CARRO',  4,  187000, 'INATIVO');

-- Diego: CNH D válida · Elias: CNH B vencendo em breve · Fábio: CNH D vencida
-- Validades relativas à data atual, para o cenário do RF04 funcionar em qualquer dia
INSERT INTO motoristas (nome, cpf, registro_cnh, categoria_cnh, validade_cnh, usuario_id, ativo) VALUES
  ('Diego Motorista', '52998224725', '04567812390', 'D', CURRENT_DATE + INTERVAL '600' DAY, 4, true),
  ('Elias Motorista', '11144477735', '07891234560', 'B', CURRENT_DATE + INTERVAL '20' DAY,  5, true),
  ('Fábio Motorista', '12345678909', '09988776655', 'D', CURRENT_DATE - INTERVAL '30' DAY,  6, true);

-- Solicitações de viagem (RF05). Datas relativas para respeitar a antecedência mínima (RN04).
INSERT INTO viagens (solicitante_id, destino, finalidade, saida, retorno_previsto, qtd_passageiros, status) VALUES
  (2, 'Natal',     'Reunião na Reitoria',          CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '5' DAY  + INTERVAL '7' HOUR, CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '5' DAY  + INTERVAL '19' HOUR, 3,  'SOLICITADA'),
  (3, 'Mossoró',   'Visita técnica com a turma',   CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '10' DAY + INTERVAL '6' HOUR, CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '10' DAY + INTERVAL '18' HOUR, 30, 'SOLICITADA'),
  (2, 'Caicó',     'Banca de TCC',                 CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '15' DAY + INTERVAL '8' HOUR, CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '16' DAY + INTERVAL '12' HOUR, 2,  'SOLICITADA');

-- Viagem já APROVADA (Cronos + Diego) no mesmo dia da viagem 1, para demonstrar os conflitos RN06/RN07
INSERT INTO viagens (solicitante_id, destino, finalidade, saida, retorno_previsto, qtd_passageiros, status, veiculo_id, motorista_id) VALUES
  (3, 'Natal', 'Entrega de documentos', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '5' DAY + INTERVAL '8' HOUR, CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '5' DAY + INTERVAL '14' HOUR, 2, 'APROVADA', 1, 1);

-- Abastecimentos anteriores (km abaixo da quilometragem atual de cada veículo)
INSERT INTO abastecimentos (veiculo_id, data, litros, valor_total, km_no_abastecimento, posto) VALUES
  (1, CURRENT_DATE - INTERVAL '20' DAY, 40.00,  248.00, 34850,  'Posto Central'),
  (1, CURRENT_DATE - INTERVAL '6' DAY,  35.50,  223.65, 35180,  'Posto Central'),
  (3, CURRENT_DATE - INTERVAL '12' DAY, 70.00,  441.00, 102100, 'Posto BR 405');
