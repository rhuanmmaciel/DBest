-- Exemplo 1: os títulos de filmes onde Brad Pitt trabalhou como ator 
select sql_no_cache  distinct m.title 
From movie m join movie_cast mc on m.movie_id = mc.movie_id
join person p on mc.person_id = p.person_id
where p.person_name='Brad Pitt';


-- Desafio 1: títulos de filmes onde Brad Pitt trabalhou como membro da equipe de produção 



-- Exemplo 2: títulos de filmes onde Brad Pitt trabalhou como ator ou como membro da equipe
select sql_no_cache  distinct title 
From movie  m
join movie_cast m_cast on m.movie_id = m_cast.movie_id 
join person p_cast on m_cast.person_id = p_cast.person_id
join  movie_crew m_crew on m.movie_id = m_crew.movie_id  
join  person p_crew on m_crew.person_id = p_crew.person_id
where (p_cast.person_name = 'Brad Pitt' or p_crew.person_name = 'Brad Pitt')
order by 1;


-- Exemplo 3: Retornar título de filmes e orçamento de filmes de um ano específico
select title, release_date, budget
from movie m use index () 
where release_year = 1978;

-- Exemplo 4: Retornar título de filmes de 1978 e os seus personagens
select title, cast_order, character_name
from movie m use index () 
join movie_cast mc on m.movie_id=mc.movie_id
where release_year = 1978;

-- Exemplo 5 igual ao exemplo 4

-- Desafio 2: Retornar título de filmes de 1978, os atores e os seus personagens


-- Exemplo 6: 
select title, cast_order, character_name
from movie m  
join movie_cast mc on m.movie_id=mc.movie_id
where release_year = 1978;

-- exemplo 7: Retornar filmes a partir de 1900 com seus personagens.
select title, cast_order, p.person_name, character_name
from movie m join movie_cast mc on m.movie_id=mc.movie_id
join person p on mc.person_id = p.person_id
where release_year > 1990;

-- exemplo 8: Retornar anos em que foram lançados filmes, desde que o ano seja inferior a 1930
select distinct release_year
from movie
where release_year < 1930;



-- parte 5: Ordem das junções
select title, p.person_name
from movie m 
join movie_cast mc on m.movie_id=mc.movie_id
join person p on mc.person_id = p.person_id;

-- parte 5: Ordem das junções (com filtro sobre release_year)
select title, p.person_name
from movie m 
join movie_cast mc on m.movie_id=mc.movie_id
join person p on mc.person_id = p.person_id
where release_year = 1978;

-- parte 5: Ordem das junções (com filtro sobre release_year e cast_order)
select title, p.person_name
from movie m 
join movie_cast mc on m.movie_id=mc.movie_id
join person p on mc.person_id = p.person_id
where release_year = 1978;

-- parte 6: voltando ao problema inicial. Solução original (ineficiente)
select sql_no_cache  distinct title 
From movie  m
join movie_cast m_cast on m.movie_id = m_cast.movie_id 
join person p_cast on m_cast.person_id = p_cast.person_id
join  movie_crew m_crew on m.movie_id = m_crew.movie_id  
join  person p_crew on m_crew.person_id = p_crew.person_id
where (p_cast.person_name = 'Brad Pitt' or p_crew.person_name = 'Brad Pitt')
order by 1;

-- desafio 3: voltando ao problema inicial. Solução modificada


-- desafio 4: títulos de filmes onde Brad Pitt trabalhou como ator e como membro da equipe
