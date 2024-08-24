package com.example.screenwiper.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.example.screenwiper.dto.*;
import com.example.screenwiper.domain.Category;
import com.example.screenwiper.domain.Member;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.request.AnalyzeRequestDto;
import com.example.screenwiper.repository.CategoryRepository;
import com.example.screenwiper.repository.MemberRepository;
import com.example.screenwiper.repository.TextDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


@Slf4j

@Service
@RequiredArgsConstructor
public class ImageAnalyzeService {

    private final AmazonS3 amazonS3;
    private final TextDataRepository textDataRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${ai.model.api.url}")
    private String aiModelApiUrl;

    public List<ResponseDto> analyzeImagesAndSave(List<MultipartFile> multipartFilelist, String dirName) throws IOException {
        // 현재 로그인한 사용자 정보를 가져옵니다.
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Long memberId = Long.parseLong(authentication.getName()); // 사용자 ID를 토큰에서 가져온다고 가정
        Long memberId = 1L; // 테스트를 위한 임시 값, 실제 사용 시에는 적절한 값으로 설정해야 합니다.

        // requestDto에서 받은 파일들을 가져옵니다.
        // List<MultipartFile> files = requestDto.getFiles();
        System.out.println("List<MultipartFile> multipartFilelist: " + multipartFilelist);

        // File uploadFile = convertMultipartFileToFile(file);
        // File uploadFile = convert(file).orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
        // String imageUrl = uploadImageToS3(file);
        // String imageUrl = uploadImageToS3(file);
        // File file = convertMultipartFileToFile(image);
        // String imageUrl = upload(file);

        //s3 올릴 이미지 객체 url로 변환 , DB 에 url 저장
        int imageNum = 1; // 이미지 번호 초기값
        List<String> imgUrlList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFilelist){
            // String fileName = createFileName(multipartFile.getOriginalFilename());

            System.out.println("multipartFile.getOriginalFilename(): " + multipartFile.getOriginalFilename());
            String fileName = multipartFile.getOriginalFilename() + "/" + UUID.randomUUID(); // S3에 저장된 파일 이름
            String filePath2 ="/Users/jordy/Desktop/workspace/test_img/test_img3.jpeg";
            File uploadFile = new File(filePath2);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());
            System.out.println("objectMetadata: " + objectMetadata);

            try(InputStream inputStream = multipartFile.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(amazonS3.getUrl(bucketName, fileName).toString());
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("imgUrlList: " + imgUrlList);

/*

        AIAnalysisResponseDto aiResponse = analyzeImage(imageUrl);
        log.info("Response ");
        log.info(aiResponse.toString());

        TextData savedData = saveTextData(memberId, aiResponse, file.getOriginalFilename(), imageUrl);

        ResponseDto responseDto = new ResponseDto(aiResponse);
        responseDto.setPhotoId(savedData.getPhotoId());
        responseDto.setMemberId(memberId);
        responseDto.setDate(savedData.getDate());

*/


        // 예시 데이터 생성
        // 예시 AIAnalysisResponseDto 객체 생성
        AIAnalysisResponseDto aiResponse = new AIAnalysisResponseDto();
        aiResponse.setCategoryId(123L);
        aiResponse.setCategoryName("Restaurant");
        aiResponse.setTitle("Amazing Sushi Place");
        aiResponse.setAddress("123 Sushi St, Tokyo, Japan");
        aiResponse.setOperatingHours(List.of(new String[]{"10 AM - 10 PM"}));
        aiResponse.setSummary("A great place to enjoy fresh sushi with a cozy atmosphere.");
        aiResponse.setPhotoName("sushi_place.jpg");
        aiResponse.setPhotoUrl("https://example.com/images/sushi_place.jpg");

        // Event 객체 생성 및 설정
        AIAnalysisResponseDto.Event event1 = new AIAnalysisResponseDto.Event();
        event1.setName("Lunch Special");
        event1.setDate("2024-08-24");

        AIAnalysisResponseDto.Event event2 = new AIAnalysisResponseDto.Event();
        event2.setName("Dinner Special");
        event2.setDate("2024-08-24");

        aiResponse.setList(Arrays.asList(event1, event2));

        // ResponseDto 객체 생성
        ResponseDto responseDto = new ResponseDto(aiResponse);
        responseDto.setPhotoId(1L);
        responseDto.setMemberId(456L);
        responseDto.setDate("2024-08-24");

        return (List<ResponseDto>) responseDto;
        // System.out.println("responseDto: " + responseDto);


    }
/*
    private String uploadImageToS3(MultipartFile image) throws IOException {
        File file = convertMultipartFileToFile(image);
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        if (!file.delete()) {
            System.err.println("Failed to delete temporary file: " + file.getAbsolutePath());
        }
        return amazonS3.getUrl(bucketName, fileName).toString();
    }
*/
/*
    // 이미지파일명 중복 방지
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

 */

    /*
    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new PrivateException(Code.WRONG_INPUT_IMAGE);
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new PrivateException(Code.WRONG_IMAGE_FORMAT);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

     */

    private String uploadImageToS3(MultipartFile image) throws IOException {
        // MultipartFile을 File로 변환
        File file = convertMultipartFileToFile(image);
        File targetFile = new File(file.getAbsolutePath());


        // 변환된 파일의 정보 로그에 출력
        System.out.println("Converted file path: " + file.getAbsolutePath());
        System.out.println("Converted file name: " + file.getName());
        System.out.println("Converted file size: " + file.length() + " bytes");

        // S3에 파일 업로드
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        System.out.println("fileName: " + fileName);

        // return fileName;
        String filePath2 ="/Users/jordy/Desktop/workspace/test_img/test_img3.jpeg";
        File uploadFile = new File(filePath2);


        // 파일 업로드
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3.getUrl(bucketName, fileName).toString();
        /*
        // 업로드 결과를 로그에 출력
        System.out.println("File uploaded successfully.");

        // 파일 삭제
        if (!file.delete()) {
            System.err.println("Failed to delete temporary file: " + file.getAbsolutePath());
        } else {
            System.out.println("Temporary file deleted successfully.");
        }

        // S3 URL 생성 및 반환
        String url = amazonS3.getUrl(bucketName, fileName).toString();
        System.out.println("File URL: " + url);

        return url;

         */


    }

    // S3로 파일 업로드하기
    public String upload(File uploadFile, String dirName) throws RuntimeException {
        // MultipartFile -> File 로 변환
        /*
        System.out.println("multipartFile: " + multipartFile);
        File uploadFile = null;
        try {
            uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("[error]: MultipartFile -> 파일 변환 실패"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

         */
        // S3에 저장된 파일 이름
        String filePath = "screenwiper/images";
        String fileName = dirName + "/" + UUID.randomUUID();
        System.out.println("fileName: " + fileName);

        String uploadImageUrl = putS3(uploadFile, fileName); // S3로 업로드
        removeOriginalFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) throws RuntimeException {
        System.out.println("putS3: ");
        System.out.println("uploadFile: " + uploadFile);
        System.out.println("fileName: " + fileName);
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    // S3 업로드 후 원본 파일 삭제
    private void removeOriginalFile(File targetFile) {
        if (targetFile.exists() && targetFile.delete()) {
            log.info("File delete success");
        } else {
            log.info("fail to remove");
        }
    }


    private Optional<File> convert(MultipartFile multipartFile) throws IOException {
        // 로컬에서 저장할 파일 경로 : user.dir => 현재 디렉토리 기준
        System.out.println("MultipartFile file: " + multipartFile);
        // String dirPath = System.getProperty("user.dir") + "/" + file.getOriginalFilename();
        //File convertFile = new File(dirPath);
        File convertFile = new File(multipartFile.getOriginalFilename());
        System.out.println("convertFile: " + convertFile);

        if (convertFile.createNewFile()) {
            // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }


    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }

    private AIAnalysisResponseDto analyzeImage(String imageUrl) {
        AnalyzeRequestDto request = new AnalyzeRequestDto(imageUrl);
        return restTemplate.postForObject(aiModelApiUrl + "/analyze_image", request, AIAnalysisResponseDto.class);
    }
    private TextData saveTextData(Long memberId, AIAnalysisResponseDto aiResponse, String photoName, String photoUrl) {
        TextData textData = new TextData();

        Optional<Member> memberOptional = memberRepository.findById(memberId);
        Member member = memberOptional.orElseThrow(() -> new RuntimeException("Member not found"));
        textData.setMember(member);

        Optional<Category> categoryOptional = categoryRepository.findById(aiResponse.getCategoryId());
        Category category = categoryOptional.orElseThrow(() -> new RuntimeException("Category not found"));
        textData.setCategory(category);

        textData.setCategoryName(aiResponse.getCategoryName());
        textData.setTitle(aiResponse.getTitle());
        textData.setAddress(aiResponse.getAddress());
        textData.setOperatingHours(aiResponse.getOperatingHours().toString());
        textData.setList(aiResponse.getList().stream()
                .map(event -> event.getName() + ": " + event.getDate())
                .collect(Collectors.toList()));
        textData.setSummary(aiResponse.getSummary());
        textData.setPhotoName(photoName);
        textData.setPhotoUrl(photoUrl);
        textData.setDate(String.valueOf(LocalDateTime.now()));

        return textDataRepository.save(textData);
    }

    public List<String> uploadImagesAndGetUrls(TestUploadRequestDto requestDto) throws IOException {
        List<MultipartFile> files = requestDto.getFiles();
        return files.stream().map(file -> {
            try {
                System.out.println("줄 확인: ");
                return uploadImageToS3(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }).collect(Collectors.toList());
    }
}
