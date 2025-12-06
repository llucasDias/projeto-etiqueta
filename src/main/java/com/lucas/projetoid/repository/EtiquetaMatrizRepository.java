package com.lucas.projetoid.repository;

import com.lucas.projetoid.model.EtiquetaMatrizEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EtiquetaMatrizRepository  extends JpaRepository<EtiquetaMatrizEntity, Integer>
{

     /**
      * Verifica se já existe um registro com determinado checkid.
      * Isso evita duplicidade de importação.
      * @param checkid identificador da última OP importada
      * @return true se já existe, false caso contrário
      **/
     boolean existsBycheckid(Integer checkid);


     /**
      * Retorna o maior valor de checkid já importado na tabela intermediária.
      * Esse valor é usado como referência para importações incrementais,
      * garantindo que apenas novas ordens sejam buscadas no Protheus.
      **/

     @Query("SELECT MAX(s.checkid) FROM EtiquetaMatrizEntity s")
     Integer findMaxCheckIdAsNumber();

     /* Buscar etiqueta pelo checkId (RECNO do Protheus) */
     Optional<EtiquetaMatrizEntity> findByCheckid(Integer checkid);

     /* Buscar todas as infos salvas no banco pelo pedido de venda no protheus */
     List<EtiquetaMatrizEntity> findByPedido(String pedido);

     /* Listar etiquetas já geradas com status = true */
     Page<EtiquetaMatrizEntity> findByStatus(boolean status, Pageable pageable);

     /* Buscar pelo código da etiqueta */
     Optional<EtiquetaMatrizEntity> findByCodigoEtiqueta(String codigoEtiqueta);


     /* Lista de etiquetas apos a data de corte */
     Page<EtiquetaMatrizEntity> findByDataAfter(LocalDateTime data, Pageable pageable);

     /* Etiquetas geradas a partir da data de corte*/
     Page<EtiquetaMatrizEntity> findByStatusAndDataAfter(boolean status, LocalDateTime data, Pageable pageable);


}
