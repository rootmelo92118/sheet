import java.util.regex.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner; 

class IdChecker{
	private int RegionAndGenderCodeToNumberConverter(char character) {
		if(character == 'I') {
			return 34;
		}else if(character == 'O') {
			return 35;
		}else if('A' <= character & character <= 'I') {
			return (int) character - '7';
		}else if('J' <= character & character <= 'N') {
			return (int) character - '8';
		}else if('P' <= character & character <= 'Z') {
			return (int) character - '9';
		}else{
			return 2147483647;
		}
	}

	private List<Object> NationalUnifiedIdNoChecker(String id, boolean old) {
		int[] idArray = new int[10];
		int[] weighting = {1,9,8,7,6,5,4,3,2,1};
		int sum = 0;
		int errorValue = 2147483647;
		for(int i = 0;i < 10;i += 1) {
			if(i == 0) {
				idArray[i] = RegionAndGenderCodeToNumberConverter(id.charAt(i));
				sum += idArray[i] / 10 * weighting[i] + idArray[i] % 10 * weighting[i + 1];
			}else if(i == 1) {
				if(old == true) {
					idArray[i] = RegionAndGenderCodeToNumberConverter(id.charAt(i));
					sum += idArray[i] * weighting[i + 1] % 10;
				}else {
					idArray[i] = (int) id.charAt(i) - '0';
					int sumTemp = idArray[i] * weighting[i + 1];
					sumTemp = sumTemp >= 10 ? sumTemp % 10 : sumTemp;
					sum += sumTemp;
				}
			}else if(i >= 2 & i <= 8) {
				idArray[i] = (int) id.charAt(i) - '0';
				int sumTemp = idArray[i] * weighting[i + 1];
				sumTemp = sumTemp >= 10 ? sumTemp % 10 : sumTemp;
				sum += sumTemp;
			}else {
				idArray[i] = (int) id.charAt(i) - '0';
				sum += idArray[i];
			}
		}
		boolean isPass = sum % 10 == 0;
		if(!isPass) {
			errorValue = 10 - (sum - idArray[9]) % 10;
		}
		return Arrays.asList(isPass, errorValue);
	}

	private List<Object> NationalIdCheck(String id) {
		String reason = "";
		boolean result = true;
		List<Object> isPass = NationalUnifiedIdNoChecker(id, false);
		if((boolean) isPass.get(0) == true) {
			reason = "輸入符合身分證字號類且檢核碼正確";
		}else {
			reason = "編碼為個人身分證字號類，檢核碼錯誤，正確檢核碼為" + isPass.get(1);
			result = false;
		}
		return Arrays.asList(result, reason);
	}

	private List<Object> UnifiedIdNoCheck(String id) {
		String reason = "";
		boolean result = true;
		List<Object> isPass = NationalUnifiedIdNoChecker(id, false);
		if((boolean) isPass.get(0) == true) {
			reason = "輸入符合新版統一證號類且檢碼正確";
		}else {
			reason = "編碼為新版統一證號類，檢核碼錯誤，正確檢核碼為" + isPass.get(1);
			result = false;
		}
		return Arrays.asList(result, reason);
	}

	private List<Object> UnifiedIdOldNoCheck(String id) {
		String reason = "";
		boolean result = true;
		List<Object> isPass = NationalUnifiedIdNoChecker(id, true);
		if((boolean) isPass.get(0) == true) {
			reason = "輸入符合舊版統一證號類且檢碼正確";
		}else {
			reason = "編碼為舊版統一證號類，檢核碼錯誤，正確檢核碼為" + isPass.get(1);
			result = false;
		}
		return Arrays.asList(result, reason);
	}

	private List<Object> BANoCheck(String bANo) {
		String reason = "";
		boolean result = true;
		int[] bANoArray = new int[8];
		int sum = 0;
		int[] weighting = {1,2,1,2,1,2,4,1};
		for(int i = 0;i < 8;i += 1) {
			bANoArray[i] = (int) bANo.charAt(i) - '0';
			int temp = bANoArray[i] * weighting[i];
			int sumTemp = temp >= 10 ? temp / 10 + temp % 10 : temp;
			sumTemp = sumTemp == 10 ? 0 : sumTemp;
			sum += sumTemp;
		}
		boolean isPass = (sum % 10 == 0 | ((sum + 1) % 10 == 0 & bANoArray[6] == 7));
		if(isPass == true) {
			reason = "輸入符合法人統編類且符合規定";
		}else {
			reason = "編碼為法人統編類，但編碼不符合規定";
			result = false;
		}
		return Arrays.asList(result, reason);
	}
	
	public boolean check(String inputData){
		String nIdPattern = "^[A-Z][1,2]\\d{8}$";
		String uIdNoPattern = "^[A-Z][8,9]\\d{8}$";
		String uIdOldNoPattern = "^[A-Z][ABCD]\\d{8}$";
		String bANoPattern = "^\\d{8}$";
		String fTaxNoPattern = "^\\d{8}[A-Z]{2}$";
		String reason= "";
		boolean result = false;
		if(Pattern.matches(nIdPattern, inputData)) {
			List<Object> rawResult = NationalIdCheck(inputData);
			result = (boolean) rawResult.get(0);
			reason = (String) rawResult.get(1);
		}else if(Pattern.matches(uIdNoPattern, inputData)) {
			List<Object> rawResult = UnifiedIdNoCheck(inputData);
			result = (boolean) rawResult.get(0);
			reason = (String) rawResult.get(1);
		}else if(Pattern.matches(uIdOldNoPattern, inputData)) {
			List<Object> rawResult = UnifiedIdOldNoCheck(inputData);
			result = (boolean) rawResult.get(0);
			reason = (String) rawResult.get(1);
		}else if(Pattern.matches(bANoPattern, inputData)) {
			List<Object> rawResult = BANoCheck(inputData);
			result = (boolean) rawResult.get(0);
			reason = (String) rawResult.get(1);
		}else if(Pattern.matches(fTaxNoPattern, inputData)) {
			reason = "輸入符合外國人稅籍編號類";
			result = true;
		}else {
			reason = "不符合任何種類";
		}
		System.out.printf("%s\n", reason);
		return result;
	}
}

public class Main
{
    
	public static void main(String[] args) {
		IdChecker idChecker = new IdChecker();
		Scanner sc = new Scanner(System.in);
        while(true){
			System.out.print("# ");
			String id = sc.nextLine();
            boolean condiction = idChecker.check(id);
            String result = "";
            if(condiction == true){
                result = "True";
            }else{
                result = "False";
            }
            System.out.println(id + " : " + result);
        }
	}
}