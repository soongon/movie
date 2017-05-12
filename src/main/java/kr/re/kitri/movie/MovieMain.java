package kr.re.kitri.movie;

import kr.re.kitri.movie.model.Item;
import kr.re.kitri.movie.service.MovieService;

import java.util.List;

/**
 * Created by danawacomputer on 2017-05-12.
 */
public class MovieMain {

    public static void main(String[] args) {

        //서비스 초기화 하면서 드라이버 로딩 해놓음..
        MovieService service = new MovieService();

        //사용자로 부터 검색어를 받는다.
        String keyword =  service.getKeywordFromUser();
        //String keyword = "마블";

        //검색어로 네이버 API호출 하여 JSON 스트링을 확보
        String json =  service.getJsonFromNaverMovieApi(keyword);
        //System.out.println(json);

        //JSON 스트링으로 데이터를 확보하여 자바 컬렉션으로 옮겨담는다.
        // List<Item>
        String lastBuildDate = service.getLastBuildDateFromJson(json);
        //System.out.println(lastBuildDate);
        int total = service.getTotalFromJson(json);
        //System.out.println(total);
        List<Item> list = service.getItemListFromJson(json);

        //자바 컬렉션의 내용을 데이터베이스에 인서트 한다.
        service.insertDataToPostgres(lastBuildDate, total, keyword, list);

    }
}
