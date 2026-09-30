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
INSERT INTO motoristas (nome, cpf, registro_cnh, categoria_cnh, validade_cnh, usuario_id, ativo) VALUES
  ('Diego Motorista', '52998224725', '04567812390', 'D', '2028-05-10', 4, true),
  ('Elias Motorista', '11144477735', '07891234560', 'B', '2026-10-25', 5, true),
  ('Fábio Motorista', '12345678909', '09988776655', 'D', '2026-08-01', 6, true);
