package edu.iis.mto.staticmock;

import edu.iis.mto.staticmock.reader.NewsReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class, NewsReaderFactory.class})
public class NewsLoaderTest {
    NewsReaderFactory newsReaderFactory;
    ConfigurationLoader configurationLoader;
    Configuration configuration;
    NewsReader newsReader;
    NewsLoader newsLoader;
    PublishableNews publishableNews;
    Field field;

    @Before
    public void setUp() throws Exception {
        mockStatic( ConfigurationLoader.class );
        configurationLoader = mock( ConfigurationLoader.class );
        when( ConfigurationLoader.getInstance() ).thenReturn( configurationLoader );

        mockStatic( NewsReaderFactory.class );
        newsReaderFactory = mock( NewsReaderFactory.class );

        newsReader = mock( NewsReader.class );
        newsLoader = new NewsLoader();

        configuration = mock( Configuration.class );
        when( configurationLoader.loadConfiguration() ).thenReturn( configuration );
        when( configuration.getReaderType() ).thenReturn( "" );

        when( newsReaderFactory.getReader( anyString() ) ).thenReturn( newsReader );
        publishableNews = PublishableNews.create();


    }

    @Test
    public void testShouldReturnOneNewsForPublic() throws IllegalAccessException, NoSuchFieldException {
        IncomingInfo incomingInfo = new IncomingInfo( "Info1", SubsciptionType.NONE );
        IncomingNews incomingNews = new IncomingNews();
        incomingNews.add( incomingInfo );
        when( newsReader.read() ).thenReturn( incomingNews );
        field = PublishableNews.class.getDeclaredField( "publicContent" );
        field.setAccessible( true );
        List<String> content = (List<String>) field.get( newsLoader.loadNews() );
        assertThat( content.size(), is( 1 ) );
    }
}