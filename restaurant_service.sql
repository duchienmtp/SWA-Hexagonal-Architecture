Insert into restaurants values
('c41fa001-eb7b-4bbf-910a-50a1c6b2e1d1', 'Restaurant 1'),
('aea3b384-e629-4c70-84dd-74476e18c406', 'Restaurant 2');

Insert into products values
('d215b5f8-0249-4dc5-89a3-51fd148cfb48', 'Product 1'),
('42cc957e-efb0-46a8-ab7d-50cf4b17641c', 'Product 2'),
('b9f138af-59f0-47a2-89e7-5f80de434804', 'Product 3');

Insert into inventories (restaurant_id, product_id, price, quantity) values
('c41fa001-eb7b-4bbf-910a-50a1c6b2e1d1', 'd215b5f8-0249-4dc5-89a3-51fd148cfb48', 50.00, 50),
('c41fa001-eb7b-4bbf-910a-50a1c6b2e1d1', '42cc957e-efb0-46a8-ab7d-50cf4b17641c', 50.00, 40),
('c41fa001-eb7b-4bbf-910a-50a1c6b2e1d1', 'b9f138af-59f0-47a2-89e7-5f80de434804', 50.00, 30),
('aea3b384-e629-4c70-84dd-74476e18c406', 'd215b5f8-0249-4dc5-89a3-51fd148cfb48', 70.00, 60),
('aea3b384-e629-4c70-84dd-74476e18c406', '42cc957e-efb0-46a8-ab7d-50cf4b17641c', 60.00, 70),
('aea3b384-e629-4c70-84dd-74476e18c406', 'b9f138af-59f0-47a2-89e7-5f80de434804', 55.00, 80);
