package com.atom.watchremotefile.mapper;

import com.atom.watchremotefile.entity.FileContent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Atom
 */

public interface FileContentMapper {
    @Insert("INSERT INTO file_content (file_path, content) VALUES (#{filePath}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFileContent(FileContent fileContent);

    @Select("SELECT * FROM file_content WHERE file_path = #{remoteFilePath}")
    FileContent findFileContentByFilePath(@Param("remoteFilePath") String remoteFilePath);
}