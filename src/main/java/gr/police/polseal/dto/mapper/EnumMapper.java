package gr.police.polseal.dto.mapper;

//import gr.police.polseal.dto.CompoundTransactionFileTypeDto;
import gr.police.polseal.dto.EnumDto;
//import gr.police.polseal.model.CompoundTransactionFileType;
//import gr.police.polseal.model.Gender;
import gr.police.polseal.model.TransactionType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EnumMapper {

//  default EnumDto toDto(Gender source) {
//    if (source == null) {
//      return null;
//    }
//    return new EnumDto(source.name(), source.getDescription());
//  }
//
//  default Gender toGender(EnumDto enumDto) {
//    if (enumDto == null || enumDto.getId() == null) {
//      return null;
//    }
//    return Gender.valueOf(enumDto.getId());
//  }
//
//  default EnumDto toDto(TransactionType source) {
//    if (source == null) {
//      return null;
//    }
//    return new EnumDto(source.name(), source.getDescription());
//  }
//
//  default TransactionType toTransactionType(EnumDto enumDto) {
//    if (enumDto == null || enumDto.getId() == null) {
//      return null;
//    }
//    return TransactionType.valueOf(enumDto.getId());
//  }
//
//  default EnumDto toDto(CompoundTransactionFileType source) {
//    if (source == null) {
//      return null;
//    }
//    return new EnumDto(source.name(), source.getDescription());
//  }
//
//  default CompoundTransactionFileType toCompoundTransactionFileType(EnumDto enumDto) {
//    if (enumDto == null || enumDto.getId() == null) {
//      return null;
//    }
//    return CompoundTransactionFileType.valueOf(enumDto.getId());
//  }
//
//  default CompoundTransactionFileTypeDto toCompoundTransactionFileTypeDto(CompoundTransactionFileType source) {
//    if (source == null) {
//      return null;
//    }
//    return new CompoundTransactionFileTypeDto(source.name(), source.getDescription(), source.isGenerated());
//  }

}
