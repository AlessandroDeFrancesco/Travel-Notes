//
//  Element.h
//  Travel Notes
//
//  Created by gianpaolo on 06/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#ifndef Element_h
#define Element_h

#import <Foundation/Foundation.h>

typedef enum elementType {VIDEO, IMAGE, NOTE} ElementType;

@interface Element : NSObject{
    NSString* content;
    float longitude;
    float latitude;
    NSDate* date;
    ElementType type;
    NSString* ownerName;
    int idElement;
}

@property (nonatomic,strong) NSString* content;
@property (nonatomic) int idElement;
@property (nonatomic) float longitude;
@property (nonatomic) float latitude;
@property (nonatomic,strong) NSDate* date;
@property (nonatomic) ElementType type;
@property (nonatomic,strong) NSString* ownerName;

- (Element*) initWithElementType : (ElementType) newType;
- (Element*) initWithElement : (Element*) element;

@end
#endif /* Element_h */
