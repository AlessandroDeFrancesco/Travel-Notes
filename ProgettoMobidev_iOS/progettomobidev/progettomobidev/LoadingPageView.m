//
//  LoadingPageView.m
//  Travel Notes
//
//  Created by gianpaolo on 06/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "LoadingPageView.h"

@implementation LoadingPageView


-(void)didMoveToSuperview{
    CGAffineTransform transform = CGAffineTransformMakeScale(3.0f, 3.0f);
    _grayActivityIndicator.transform = transform;
    _grayActivityIndicator.color = [UIColor blueColor];
}


@end
