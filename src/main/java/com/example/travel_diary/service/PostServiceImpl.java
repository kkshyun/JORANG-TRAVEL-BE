package com.example.travel_diary.service;

import com.example.travel_diary.global.domain.entity.Post;
import com.example.travel_diary.global.domain.entity.User;
import com.example.travel_diary.global.domain.repository.PostRepository;
import com.example.travel_diary.global.domain.type.Scope;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    // 여행 일지 작성 누르면 바로 post id를 생성 시킴, 업데이트도 작성일자만 갱신
    @Override
    @Transactional
    public Long createPost(@AuthenticationPrincipal User user) {
        Post post = postRepository.save(Post.builder().user(user).build());
        return post.getId();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(Long id, String title) {
        Post post = postRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        post.setTitle(title);
        post.setCreatedAt(LocalDateTime.now());
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAllByDiaries_Scope(Scope.PUBLIC);
    }

    @Override
    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }


    @Override
    public List<Post> getRecentPostsFirst() {
        return postRepository.findByDiaries_ScopeOrderByCreatedAtDesc(Scope.PUBLIC);
    }

    @Override
    public List<Post> getRecent5PostsByCountry(String country) {
        return postRepository.findTop5ByDiaries_ScopeAndDiaries_CountryOrderByCreatedAtDesc(Scope.PUBLIC, country);
    }

    @Override
    public List<Post> getRecentPostsFirstByCountry(String country) {
        return postRepository.findAllByDiaries_ScopeAndDiaries_CountryOrderByCreatedAtDesc(Scope.PUBLIC, country.toLowerCase());
    }


    @Override
    public List<Post> getTop5LikeOnThisWeek() {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return postRepository.findTop5ByDiaries_ScopeAndCreatedAtBetweenOrderByLoveDesc(Scope.PUBLIC, startOfWeek, endOfWeek);
    }

    @Override
    public List<Post> getRecentPostsFirstBetweenTheseDates(LocalDate from, LocalDate to) {
        return postRepository.findAllByDiaries_ScopeAndDiaries_DateBetweenOrderByCreatedAtDesc(Scope.PUBLIC, from, to);
    }

    @Override
    public List<Post> getAllByUser(User user) {
        return postRepository.findAllByUser(user);
    }
}
