package serviecesTests;

import org.example.exceptions.InvalidUrlException;
import org.example.repositories.entities.UrlEntity;
import org.example.repositories.postgres.DbRepository;
import org.example.services.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServiceTest {

    private Service service;
    private DbRepository repository;
    private final String validUrl = "https://url.com";
    private final String invalidUrl = "url";

    @BeforeEach
    public void inti(){
        repository = Mockito.mock(DbRepository.class);
        when(repository.check(validUrl)).thenReturn(false);
        when(repository.save(new UrlEntity(validUrl))).thenReturn(1L);
        when(repository.check(1L)).thenReturn(true);
        when(repository.get(1L)).thenReturn(new UrlEntity(validUrl));
        service = new Service(repository);
    }


    @Test
    public void addTest(){
        String str = service.add(validUrl);
        assertThat(str).isNotNull();

        String str2 = service.add(validUrl);
        assertThat(str2).isNotNull();
        assertThat(str).isEqualTo(str2);
        verify(repository,  times(2)).save(any());
    }

    @Test
    public void addInvalidTest(){
        assertThatThrownBy(() -> service.add(invalidUrl)).isInstanceOf(InvalidUrlException.class);
    }

    @Test
    public void getTest(){
        String str = service.add(validUrl);
        String old = service.get(str);
        assertThat(old).isNotNull();
        assertThat(old).isEqualTo(validUrl);
        verify(repository, times(1)).get(1L);
    }


}
