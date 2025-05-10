package com.eroom.directory.dto;

import java.time.LocalDateTime;

import com.eroom.directory.entity.Directory;
import com.eroom.directory.entity.DirectoryBookmark;
import com.eroom.employee.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DirectoryBookmarkDto {

    private Long directoryBookmarkNo; // 직원 북마크 번호
    private String directoryBookmarkYn; // 북마크 여부
    private String directoryBookmarkCreator; // 북마크 생성자
    private String directoryBookmarkEditor; // 수정자
    private String visibleYn; // 사용여부
    private LocalDateTime directoryBookmarkRegDate; // 생성시간
    private LocalDateTime directoryBookmarkModDate; // 수정시간

    private Directory directory;
    private Employee employee;
    
    private Long directory_no;

    public static DirectoryBookmarkDto toDto(DirectoryBookmark entity) {
        return DirectoryBookmarkDto.builder()
                .directoryBookmarkNo(entity.getDirectory_bookmark_no())
                .directoryBookmarkYn(entity.getDirectoryBookmarkYn())
                .directoryBookmarkCreator(entity.getDirectoryBookmarkCreator())
                .directoryBookmarkEditor(entity.getDirectoryBookmarkEditor())
                .visibleYn(entity.getVisibleYn())
                .directoryBookmarkRegDate(entity.getDirectoryBookmarkRegDate())
                .directoryBookmarkModDate(entity.getDirectoryBookmarkModDate())
                .directory(entity.getDirectory())
                .employee(entity.getEmployee())
                .build();
    }

    public DirectoryBookmark toEntity() {
        return DirectoryBookmark.builder()
                .directory_bookmark_no(this.directoryBookmarkNo)
                .directoryBookmarkYn(this.directoryBookmarkYn)
                .directoryBookmarkCreator(this.directoryBookmarkCreator)
                .directoryBookmarkEditor(this.directoryBookmarkEditor)
                .visibleYn(this.visibleYn)
                .directoryBookmarkRegDate(this.directoryBookmarkRegDate)
                .directoryBookmarkModDate(this.directoryBookmarkModDate)
                .directory(this.directory)
                .employee(this.employee)
                .build();
    }

}
