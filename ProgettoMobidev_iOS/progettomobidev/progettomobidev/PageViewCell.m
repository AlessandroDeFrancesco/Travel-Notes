//
//  PageViewCell.m
//  Journal
//
//  Created by ianfire on 23/10/16.
//  Copyright © 2016 ianfire. All rights reserved.
//

#import <CoreLocation/CoreLocation.h>
#import <QuartzCore/QuartzCore.h>
#import "GeoLocationUtility.h"
#import "PageViewCell.h"
#import "WallViewController.h"
#import "Utility.h"
#import "FontUtility.h"

@implementation PageViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // setto l'immagine di sfondo, mettendo gli angoli non allungabili
    UIImage * backgroundImg = [[UIImage imageNamed:@"page.png"] resizableImageWithCapInsets:UIEdgeInsetsMake(25,25,25,25) resizingMode:UIImageResizingModeStretch];
    [_pageBackground setImage:backgroundImg];
    // idem per gli sticker
    UIImage * stickersImg = [[UIImage imageNamed:@"Stickers.png"] resizableImageWithCapInsets:UIEdgeInsetsMake(25,25,25,25) resizingMode:UIImageResizingModeStretch];
    [_stickersImage setImage:stickersImg];
    
    [self.placeButton addTarget:self action:@selector(didTapButton:) forControlEvents:UIControlEventTouchUpInside];
    [self.buttonShare addTarget:self action:@selector(didTapButtonSharing:) forControlEvents:UIControlEventTouchUpInside];
    
    // settaggio per video
    videoPlayer = [AVPlayer playerWithPlayerItem:nil];
    AVPlayerLayer *layer = [AVPlayerLayer layer];
    [layer setPlayer:videoPlayer];
    [layer setFrame:_contentVideo.frame];
    [layer setBackgroundColor:[UIColor blackColor].CGColor];
    [layer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
    [_contentVideo.layer addSublayer:layer];
    UITapGestureRecognizer *playPauseOnTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(playPauseVideo)];
    [_contentVideo addGestureRecognizer:playPauseOnTap];
    
    // settaggio font
    _textName.font = [[FontUtility getInstance] getFont:TITLE andSize:17];
    _textHour.font = [[FontUtility getInstance] getFont:EXTRA andSize:11];
    _textPlace.font = [[FontUtility getInstance] getFont:EXTRA andSize:12];
    _contentText.font = [[FontUtility getInstance] getFont:TEXT andSize:14];
    
    [[self expandButton]setHidden:YES];
}

- (void)fillWithElement:(Element *)element{
    [_textName setText: element.ownerName];
    [_textHour setText: [NSDateFormatter localizedStringFromDate:element.date
                                                       dateStyle:NSDateFormatterNoStyle
                                                       timeStyle:NSDateFormatterShortStyle]];
    
    // aggiungo l'elemento vero e proprio alla view adatta a seconda del tipo
    [_contentVideo setHidden:YES];
    [_contentImage setHidden:YES];
    [_contentText setHidden:YES];
    [_playPauseButton setHidden:YES];
    [_stickersImage setHidden:YES];
    
    if(element.content != nil){
        switch (element.type) {
            case NOTE:{
                [_contentText setHidden:NO];
                [_contentText setText : element.content];
                [self centerTextVertically:_contentText];
                [[self expandButton]setHidden:YES];
                break;
            }
            case VIDEO:{
                NSURL* url = [NSURL fileURLWithPath:element.content];
                [[self expandButton]setHidden:NO];
                [[self stickersImage]setHidden:NO];
                // espandi il video
                [[self expandButton] addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(expandElement)]];
                
                [self showVideo:url];
                break;
            }
            case IMAGE:{
                NSData *data = [Utility loadDataContent:element.content];
                [[self expandButton]setHidden:NO];
                [[self stickersImage]setHidden:NO];
                // espandi l'immagine
                [[self expandButton] addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(expandElement)]];
                
                [self showImage:[UIImage imageWithData:data]];
                break;
            }
        }
    }
    
    [self setLocation: element.latitude andLongitude: element.longitude];
    
}

- (void) setLocation:(float)latitude andLongitude:(float)longitude{
    // LOCATION
    GeoLocationUtility* geoLocation = [GeoLocationUtility getInstance];
    [geoLocation getGeoLocation:latitude andNewLong:longitude andLocationCallback:^(NSString * address) {
         if(address){
             [_textPlace setText: address];
         }
     }];
}

-(void) setNoteEditable:(BOOL)editable{
    _contentText.delegate = self;
    [_contentText setEditable:editable];
    [_contentText setHidden:NO];
    [_stickersImage setHidden:YES];

}

-(void)showImage:(UIImage *)image{
    _contentImage.image=image;
    [_contentImage setContentMode:UIViewContentModeScaleAspectFill];
    [_contentImage setClipsToBounds:YES];
    [_stickersImage setHidden:NO];
    [_contentImage setHidden:false];
}

- (void)expandElement {
    if(![_contentImage isHidden]){
        UIImageView *expandedImage = [[UIImageView alloc]initWithFrame:self.superview.superview.frame];
        expandedImage.backgroundColor = [UIColor blackColor];
        expandedImage.center = self.superview.superview.center;
        expandedImage.contentMode = UIViewContentModeScaleAspectFit;
        expandedImage.image = _contentImage.image;
        expandedImage.userInteractionEnabled = YES;
        [expandedImage addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(closeExpandedElement:)]];
        
        [self.superview.superview.window addSubview:expandedImage];
    } else {
        UIView *expandedVideo = [[UIImageView alloc]initWithFrame:self.superview.superview.frame];
        expandedVideo.backgroundColor = [UIColor blackColor];
        expandedVideo.center = self.superview.superview.center;
        expandedVideo.userInteractionEnabled = YES;
        
        AVPlayer *player = [AVPlayer playerWithPlayerItem:videoPlayer.currentItem.copy];
        AVPlayerLayer *layer = [AVPlayerLayer layer];
        [layer setPlayer:player];
        [layer setFrame:self.superview.superview.frame];
        [layer setBackgroundColor:[UIColor blackColor].CGColor];
        [layer setVideoGravity:AVLayerVideoGravityResizeAspect];
        [expandedVideo.layer addSublayer:layer];
        [player play];
        
        [expandedVideo addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(closeExpandedElement:)]];
        
        [self.superview.superview.window addSubview:expandedVideo];
    }
}

- (void) closeExpandedElement:(id)sender {
    [[sender view] removeFromSuperview];
}

-(void)showVideo:(NSURL *)videoUrl{
    AVPlayerItem* videoItem = [[AVPlayerItem alloc] initWithURL:videoUrl];
    [videoPlayer replaceCurrentItemWithPlayerItem:videoItem];
    [_contentVideo setHidden:false];
    // per essere notificati quando il video finisce
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(videoDidFinishPlaying:) name:AVPlayerItemDidPlayToEndTimeNotification object:videoItem];
    
    [_playPauseButton setHidden:false];
    [_stickersImage setHidden:NO];

}

-(void)videoDidFinishPlaying:(NSNotification *) notification {
    [videoPlayer seekToTime:kCMTimeZero];
    [_playPauseButton setHidden:false];
}

-(void)centerTextVertically:(UITextView*)tv{
    CGFloat deadSpace = ([tv bounds].size.height - [tv contentSize].height);
    CGFloat inset = MAX(0, deadSpace/2.0);
    tv.contentInset = UIEdgeInsetsMake(inset, tv.contentInset.left, inset, tv.contentInset.right);
}

- (void)playPauseVideo {
    if(videoPlayer.rate == 0){
        [videoPlayer play];
        [_playPauseButton setHidden:true];
    } else {
        [videoPlayer pause];
        [_playPauseButton setHidden:false];
    }
    
    
    NSLog(@"error video %@", videoPlayer.currentItem.error.localizedDescription);
}

- (void)didTapButton:(id)sender {
    if (self.didTapButtonBlock)
    {
        self.didTapButtonBlock(sender);
    }
}

- (void)didTapButtonSharing:(id)sender {
    if (self.didTapButtonBlockSharing)
    {
        self.didTapButtonBlockSharing(sender);
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range
 replacementText:(NSString *)text
{
    // Any new character added is passed in as the "text" parameter
    if ([text isEqualToString:@"\n"]) {
        // Be sure to test for equality using the "isEqualToString" message
        [textView resignFirstResponder];
        
        return FALSE;
    }
    return TRUE;
}

@end
