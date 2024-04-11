select sql_no_cache  distinct title 
From movie  m 
join movie_cast m_cast on m.movie_id = m_cast.movie_id 
join person p_cast on m_cast.person_id = p_cast.person_id
join  movie_crew m_crew on m.movie_id = m_crew.movie_id  
join  person p_crew on m_crew.person_id = p_crew.person_id
where (p_cast.person_name = 'Brad Pitt' or p_crew.person_name = 'Brad Pitt')
order by 1;


-- modificação na tabela movie

alter table movie add column release_year int null;
update movie set release_year = year(release_date);
create index idx_year on movie(release_year);

-- Exemplo 1: os títulos de filmes onde Brad Pitt trabalhou como ator 
select sql_no_cache  distinct m.title 
From movie m join movie_cast mc on m.movie_id = mc.movie_id
join person p on mc.person_id = p.person_id
where p.person_name='Brad Pitt';


-- Desafio 1: títulos de filmes onde Brad Pitt trabalhou como membro da equipe de produção 
select sql_no_cache  distinct m.title 
From movie m join movie_crew mc on m.movie_id = mc.movie_id
join person p on mc.person_id = p.person_id
where p.person_name='Brad Pitt';


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
select title, cast_order, p.person_name, character_name
from movie m use index () 
join movie_cast mc on m.movie_id=mc.movie_id
join person p on mc.person_id = p.person_id
where release_year = 1978;

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
select sql_no_cache title from (
       select title from movie m,  movie_crew m_crew, person p_crew
       where m.movie_id = m_crew.movie_id and m_crew.person_id = p_crew.person_id and   
       p_crew.person_name = 'Brad Pitt'
   Union
       select title from movie m, movie_cast m_cast, person p_cast
       where m.movie_id = m_cast.movie_id and m_cast.person_id = p_cast.person_id and 
       p_cast.person_name = 'Brad Pitt'
) as x
order by 1;

-- desafio 4: títulos de filmes onde Brad Pitt trabalhou como ator e como membro da equipe
select sql_no_cache title
from movie m 
join movie_crew mc on m.movie_id = mc.movie_id
join person p on mc.person_id = p.person_id
where  p.person_name = 'Brad Pitt'
and title IN (       
    select title       
   from movie m 
   join movie_cast mc on m.movie_id = mc.movie_id
   join person p on mc.person_id = p.person_id       
   where p.person_name = 'Brad Pitt'
) 
order by 1;

-- SOBRAS 


select title, count(*)
from movie m join movie_cast mc on m.movie_id=mc.movie_id
where release_year = 1978
group by m.movie_id
having count(*) <10;

-- : 
select p.person_name, count(*)
from movie m
join movie_cast mc on m.movie_id = mc.movie_id
join person p on mc.person_id = p.person_id
group by p.person_id
order by 2 desc;

-- otimizada
select p.person_name, count(*)
from movie_cast mc 
join person p on mc.person_id = p.person_id
group by p.person_id
order by 2 desc;

-- (versão muito complexa, não será usada)

-- retornar quantidade de pessoas que não atuaram em filmes com orçamento zerado
-- esta versão retorne inclusive pessoas que nunca atuaram em filmes, usando left join

select count(*)
from 
person p 
left join (
movie_cast mc join movie m on m.movie_id = mc.movie_id and m.budget = 0)
on mc.person_id = p.person_id
where m.movie_id is null
order by 1;

-- esta versão retorne inclusive pessoas que nunca atuaram em filmes, usando not in
select count(*)
from person p 
where p.person_id not in (select mc.person_id
                          from movie_cast mc join movie m on mc.movie_id = m.movie_id
						  where m.budget = 0);

-- esta versão não retorne pessoas que nunca atuaram em filmes, usando left join
select count(distinct p.person_id)
from 
person p join movie_cast mc on mc.person_id = p.person_id
left join (
movie_cast mc1 join movie m on m.movie_id = mc1.movie_id and m.budget = 0)
on mc1.person_id = p.person_id
where m.movie_id is null
order by 1;

-- esta versão não retorne pessoas que nunca atuaram em filmes, usando not in
select count(distinct p.person_id)
from person p join movie_cast mc on mc.person_id = p.person_id
where p.person_id not in (select mc.person_id
                          from movie_cast mc join movie m on mc.movie_id = m.movie_id
						  where m.budget = 0);


-- (versão mais light)

select count(*)
from 
person p 
left join movie_cast mc on mc.person_id = p.person_id
where mc.movie_id is null
order by 1;

-- esta versão retorne inclusive pessoas que nunca atuaram em filmes, usando not in
select count(*)
from person p 
where p.person_id not in (select mc.person_id
                          from movie_cast mc);


-- retornar quantidade de pessoas que não atuaram em filmes 
-- usando left join
select sql_no_cache title 
from movie m join movie_crew mc on m.movie_id = mc.movie_id
join person p on mc.person_id = p.person_id
where  p.person_name = 'Brad Pitt' 
and title in (
       select title 
       from movie m join movie_cast mc on m.movie_id = mc.movie_id
	   join person p on mc.person_id = p.person_id
       where p.person_name = 'Brad Pitt'
) order by 1;
